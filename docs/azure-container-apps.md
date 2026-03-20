# Azure Deployment

This guide deploys the full stack to Azure using:

- Azure Container Registry (ACR) for images
- Azure Container Apps for frontend and backend
- Azure Database for PostgreSQL Flexible Server for the database

## Architecture

- Frontend container: public Azure Container App
- Backend container: public Azure Container App
- PostgreSQL: managed Azure Flexible Server

The deployment script builds the backend image first, deploys it, discovers the backend URL, then builds the frontend image with `VITE_API_BASE_URL` pointing at that backend URL. After the frontend is deployed, it updates backend CORS so the frontend origin is allowed.

## Prerequisites

- Azure CLI installed
- Logged in with `az login`
- Permission to create resource groups, ACR, Container Apps, and PostgreSQL

Required Azure CLI extension:

```powershell
az extension add --name containerapp --upgrade
```

## Deploy

From the backend repo root:

```powershell
powershell -ExecutionPolicy Bypass -File .\run\deploy-azure-container-apps.ps1 `
  -SubscriptionId "<subscription-id>" `
  -ResourceGroup "rg-pft-prod" `
  -Location "centralindia" `
  -AcrName "pftacr12345" `
  -ContainerAppsEnvironment "pft-env" `
  -BackendAppName "pft-backend" `
  -FrontendAppName "pft-frontend" `
  -PostgresServerName "pft-postgres-12345" `
  -PostgresAdminUsername "pftadmin" `
  -PostgresAdminPassword "<strong-password>" `
  -JwtSecret "<base64-secret>"
```

The script creates or reuses:

- resource group
- ACR
- PostgreSQL Flexible Server
- PostgreSQL database `personal_finance_tracker`
- Container Apps environment
- backend and frontend container apps

## Important Environment Values

Backend container app:

- `DB_URL=jdbc:postgresql://<server>.postgres.database.azure.com:5432/personal_finance_tracker`
- `DB_USERNAME=<postgres-admin-username>`
- `DB_PASSWORD` from Container Apps secret
- `JWT_SECRET` from Container Apps secret
- `CORS_ALLOWED_ORIGINS=https://<frontend-fqdn>`

Frontend container build:

- `VITE_API_BASE_URL=https://<backend-fqdn>/api`

## Notes

- The script uses public ingress for both apps.
- The PostgreSQL server is created with Azure-services firewall access for fast setup.
- For production hardening, prefer private networking, managed identities where possible, and a stronger sizing review.
- Frontend rebuild is required when the backend public URL changes because `VITE_API_BASE_URL` is compiled into the frontend bundle.
