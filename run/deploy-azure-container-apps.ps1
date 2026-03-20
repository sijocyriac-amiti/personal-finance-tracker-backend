param(
    [Parameter(Mandatory = $true)]
    [string]$ResourceGroup,

    [Parameter(Mandatory = $true)]
    [string]$Location,

    [Parameter(Mandatory = $true)]
    [string]$SubscriptionId,

    [Parameter(Mandatory = $true)]
    [string]$AcrName,

    [Parameter(Mandatory = $true)]
    [string]$ContainerAppsEnvironment,

    [Parameter(Mandatory = $true)]
    [string]$BackendAppName,

    [Parameter(Mandatory = $true)]
    [string]$FrontendAppName,

    [Parameter(Mandatory = $true)]
    [string]$PostgresServerName,

    [Parameter(Mandatory = $true)]
    [string]$PostgresAdminUsername,

    [Parameter(Mandatory = $true)]
    [string]$PostgresAdminPassword,

    [Parameter(Mandatory = $true)]
    [string]$JwtSecret,

    [string]$PostgresDatabaseName,

    [string]$BackendImageName,

    [string]$FrontendImageName = "personal-finance-tracker-frontend"
)

$ErrorActionPreference = "Stop"

if (-not $PostgresDatabaseName) {
    $PostgresDatabaseName = "personal_finance_tracker"
}

if (-not $BackendImageName) {
    $BackendImageName = "personal-finance-tracker-backend"
}

if (-not $FrontendImageName) {
    $FrontendImageName = "personal-finance-tracker-frontend"
}

$projectRoot = Split-Path -Parent $PSScriptRoot
$workspaceRoot = Split-Path -Parent $projectRoot
$frontendRoot = Join-Path $workspaceRoot "personal-finance-tracker-frontend"

if (-not (Test-Path $frontendRoot)) {
    throw "Frontend repo was not found at $frontendRoot"
}

function Invoke-AzCli {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    & az @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "Azure CLI command failed: az $($Arguments -join ' ')"
    }
}

function Get-AzCliJson {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    $output = & az @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "Azure CLI command failed: az $($Arguments -join ' ')"
    }

    return $output | ConvertFrom-Json
}

function Test-AzCli {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    $previousErrorActionPreference = $ErrorActionPreference
    try {
        $ErrorActionPreference = "Continue"
        & az @Arguments "--only-show-errors" 1>$null 2>$null
        return $LASTEXITCODE -eq 0
    }
    finally {
        $ErrorActionPreference = $previousErrorActionPreference
    }
}

function Invoke-Podman {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    & podman @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "Podman command failed: podman $($Arguments -join ' ')"
    }
}

Write-Host "Selecting Azure subscription..."
Invoke-AzCli -Arguments @("account", "set", "--subscription", $SubscriptionId)

Write-Host "Ensuring required Azure CLI extensions are installed..."
Invoke-AzCli -Arguments @("extension", "add", "--name", "containerapp", "--upgrade")

Write-Host "Creating resource group if needed..."
Invoke-AzCli -Arguments @("group", "create", "--name", $ResourceGroup, "--location", $Location)

Write-Host "Creating Azure Container Registry if needed..."
if (-not (Test-AzCli -Arguments @("acr", "show", "--resource-group", $ResourceGroup, "--name", $AcrName))) {
    Invoke-AzCli -Arguments @("acr", "create", "--resource-group", $ResourceGroup, "--name", $AcrName, "--sku", "Basic", "--admin-enabled", "true")
}

$acr = Get-AzCliJson -Arguments @("acr", "show", "--resource-group", $ResourceGroup, "--name", $AcrName, "--output", "json")
$acrLoginServer = $acr.loginServer
$acrCreds = Get-AzCliJson -Arguments @("acr", "credential", "show", "--name", $AcrName, "--output", "json")
$acrUsername = $acrCreds.username
$acrPassword = $acrCreds.passwords[0].value

Write-Host "Creating Azure Database for PostgreSQL Flexible Server if needed..."
if (-not (Test-AzCli -Arguments @("postgres", "flexible-server", "show", "--resource-group", $ResourceGroup, "--name", $PostgresServerName))) {
    Invoke-AzCli -Arguments @(
        "postgres", "flexible-server", "create",
        "--resource-group", $ResourceGroup,
        "--name", $PostgresServerName,
        "--location", $Location,
        "--admin-user", $PostgresAdminUsername,
        "--admin-password", $PostgresAdminPassword,
        "--sku-name", "Standard_B1ms",
        "--tier", "Burstable",
        "--storage-size", "32",
        "--version", "16",
        "--public-access", "0.0.0.0"
    )
}

Write-Host "Ensuring PostgreSQL database exists..."
if (-not (Test-AzCli -Arguments @("postgres", "flexible-server", "db", "show", "--resource-group", $ResourceGroup, "--server-name", $PostgresServerName, "--database-name", $PostgresDatabaseName))) {
    Invoke-AzCli -Arguments @(
        "postgres", "flexible-server", "db", "create",
        "--resource-group", $ResourceGroup,
        "--server-name", $PostgresServerName,
        "--database-name", $PostgresDatabaseName
    )
}

Write-Host "Allowing Azure services to reach PostgreSQL..."
if (-not (Test-AzCli -Arguments @("postgres", "flexible-server", "firewall-rule", "show", "--resource-group", $ResourceGroup, "--name", $PostgresServerName, "--rule-name", "AllowAzureServices"))) {
    Invoke-AzCli -Arguments @(
        "postgres", "flexible-server", "firewall-rule", "create",
        "--resource-group", $ResourceGroup,
        "--name", $PostgresServerName,
        "--rule-name", "AllowAzureServices",
        "--start-ip-address", "0.0.0.0",
        "--end-ip-address", "0.0.0.0"
    )
}

Write-Host "Creating Container Apps environment if needed..."
if (-not (Test-AzCli -Arguments @("containerapp", "env", "show", "--name", $ContainerAppsEnvironment, "--resource-group", $ResourceGroup))) {
    Invoke-AzCli -Arguments @(
        "containerapp", "env", "create",
        "--name", $ContainerAppsEnvironment,
        "--resource-group", $ResourceGroup,
        "--location", $Location
    )
}

$backendImage = "${acrLoginServer}/${BackendImageName}:latest"
$frontendImage = "${acrLoginServer}/${FrontendImageName}:latest"
$dbUrl = "jdbc:postgresql://$PostgresServerName.postgres.database.azure.com:5432/$PostgresDatabaseName"
$dbUsername = "$PostgresAdminUsername"

Write-Host "Logging in to Azure Container Registry with Podman..."
Invoke-Podman -Arguments @(
    "login",
    $acrLoginServer,
    "--username", $acrUsername,
    "--password", $acrPassword
)

Write-Host "Building backend image locally with Podman..."
Invoke-Podman -Arguments @(
    "build",
    "-t", $backendImage,
    "-f", (Join-Path $projectRoot "Containerfile"),
    $projectRoot
)

Write-Host "Pushing backend image to Azure Container Registry..."
Invoke-Podman -Arguments @("push", $backendImage)

Write-Host "Deploying backend container app..."
if (-not (Test-AzCli -Arguments @("containerapp", "show", "--name", $BackendAppName, "--resource-group", $ResourceGroup))) {
    Invoke-AzCli -Arguments @(
        "containerapp", "create",
        "--name", $BackendAppName,
        "--resource-group", $ResourceGroup,
        "--environment", $ContainerAppsEnvironment,
        "--image", $backendImage,
        "--target-port", "8080",
        "--ingress", "external",
        "--registry-server", $acrLoginServer,
        "--registry-username", $acrUsername,
        "--registry-password", $acrPassword,
        "--secrets", "db-password=$PostgresAdminPassword", "jwt-secret=$JwtSecret",
        "--env-vars",
        "DB_URL=$dbUrl",
        "DB_USERNAME=$dbUsername",
        "DB_PASSWORD=secretref:db-password",
        "JWT_SECRET=secretref:jwt-secret",
        "CORS_ALLOWED_ORIGINS=https://example.invalid"
    )
}
else {
    Invoke-AzCli -Arguments @(
        "containerapp", "update",
        "--name", $BackendAppName,
        "--resource-group", $ResourceGroup,
        "--image", $backendImage,
        "--set-env-vars",
        "DB_URL=$dbUrl",
        "DB_USERNAME=$dbUsername",
        "DB_PASSWORD=secretref:db-password",
        "JWT_SECRET=secretref:jwt-secret",
        "CORS_ALLOWED_ORIGINS=https://example.invalid",
        "--secrets", "db-password=$PostgresAdminPassword", "jwt-secret=$JwtSecret"
    )
}

$backendApp = Get-AzCliJson -Arguments @(
    "containerapp", "show",
    "--name", $BackendAppName,
    "--resource-group", $ResourceGroup,
    "--output", "json"
)
$backendUrl = "https://$($backendApp.properties.configuration.ingress.fqdn)"

Write-Host "Building frontend image locally with Podman..."
Invoke-Podman -Arguments @(
    "build",
    "-t", $frontendImage,
    "-f", (Join-Path $frontendRoot "Containerfile"),
    "--build-arg", "VITE_API_BASE_URL=$backendUrl/api",
    $frontendRoot
)

Write-Host "Pushing frontend image to Azure Container Registry..."
Invoke-Podman -Arguments @("push", $frontendImage)

Write-Host "Deploying frontend container app..."
if (-not (Test-AzCli -Arguments @("containerapp", "show", "--name", $FrontendAppName, "--resource-group", $ResourceGroup))) {
    Invoke-AzCli -Arguments @(
        "containerapp", "create",
        "--name", $FrontendAppName,
        "--resource-group", $ResourceGroup,
        "--environment", $ContainerAppsEnvironment,
        "--image", $frontendImage,
        "--target-port", "80",
        "--ingress", "external",
        "--registry-server", $acrLoginServer,
        "--registry-username", $acrUsername,
        "--registry-password", $acrPassword
    )
}
else {
    Invoke-AzCli -Arguments @(
        "containerapp", "update",
        "--name", $FrontendAppName,
        "--resource-group", $ResourceGroup,
        "--image", $frontendImage
    )
}

$frontendApp = Get-AzCliJson -Arguments @(
    "containerapp", "show",
    "--name", $FrontendAppName,
    "--resource-group", $ResourceGroup,
    "--output", "json"
)
$frontendUrl = "https://$($frontendApp.properties.configuration.ingress.fqdn)"

Write-Host "Updating backend CORS allowed origins to match frontend..."
Invoke-AzCli -Arguments @(
    "containerapp", "update",
    "--name", $BackendAppName,
    "--resource-group", $ResourceGroup,
    "--set-env-vars",
    "CORS_ALLOWED_ORIGINS=$frontendUrl"
)

Write-Host "Azure deployment complete."
Write-Host "Frontend URL: $frontendUrl"
Write-Host "Backend URL:  $backendUrl"
Write-Host "PostgreSQL:   $PostgresServerName.postgres.database.azure.com"
