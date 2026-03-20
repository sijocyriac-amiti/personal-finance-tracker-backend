# Podman Deployment

This setup runs the full stack locally with:

- PostgreSQL on `localhost:5432`
- Spring Boot backend on `localhost:8080`
- React frontend on `localhost:3000`

## Prerequisites

- Podman installed
- On Windows, WSL2 support available for `podman machine`
- Windows `Virtual Machine Platform` feature enabled
- Hardware virtualization enabled in BIOS
- A compose provider installed for `podman compose`, such as `docker-compose` or `podman-compose`

Recommended Windows install:

```powershell
winget install --id Docker.DockerCompose --exact
```

## Files

- Backend container build: `Containerfile`
- Full stack compose file: `compose.podman.yaml`
- Frontend container build: `../personal-finance-tracker-frontend/Containerfile`

## Start

From the backend repo root:

```powershell
podman compose -f .\compose.podman.yaml up --build -d
```

Or use the local helper script:

```powershell
powershell -ExecutionPolicy Bypass -File .\run\deploy-podman.local.ps1
```

The helper script creates and starts a Podman machine named `personal-finance-podman` when needed.
It also refreshes `PATH` before calling `podman compose`, which helps when `docker-compose` was installed in a previous shell.

If your Podman installation uses `podman-compose` instead:

```powershell
podman-compose -f .\compose.podman.yaml up --build -d
```

## Stop

```powershell
podman compose -f .\compose.podman.yaml down
```

## Rebuild

```powershell
podman compose -f .\compose.podman.yaml up --build -d
```

## Logs

```powershell
podman compose -f .\compose.podman.yaml logs -f backend
podman compose -f .\compose.podman.yaml logs -f frontend
podman compose -f .\compose.podman.yaml logs -f db
```

## Notes

- Frontend requests go to `http://localhost:8080/api`
- Backend connects to PostgreSQL through the service name `db`
- PostgreSQL data is stored in the named volume `pft-postgres-data`
- The backend CORS allowlist is set to `http://localhost:3000`

## Windows Troubleshooting

If `podman machine init` fails with a WSL2 or `HCS_E_HYPERV_NOT_INSTALLED` error:

1. Run `wsl.exe --install --no-distribution` in an elevated terminal.
2. Enable the `Virtual Machine Platform` Windows feature.
3. Reboot if Windows asks for it.
4. Ensure CPU virtualization is enabled in BIOS.
5. Retry `powershell -ExecutionPolicy Bypass -File .\run\deploy-podman.local.ps1`.

If `podman compose` fails because no provider is installed:

1. Run `winget install --id Docker.DockerCompose --exact`.
2. Retry `powershell -ExecutionPolicy Bypass -File .\run\deploy-podman.local.ps1`.
