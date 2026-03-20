# Podman Deployment

This setup runs the full stack locally with:

- PostgreSQL on `localhost:5432`
- Spring Boot backend on `localhost:8080`
- React frontend on `localhost:3000`

## Prerequisites

- Podman installed
- Either `podman compose` or `podman-compose` available

## Files

- Backend container build: `Containerfile`
- Full stack compose file: `compose.podman.yaml`
- Frontend container build: `../personal-finance-tracker-frontend/Containerfile`

## Start

From the backend repo root:

```powershell
podman compose -f .\compose.podman.yaml up --build -d
```

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
