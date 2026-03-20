# GitHub Actions For Azure

This workspace now includes GitHub Actions workflows for split frontend and backend repos.

## Backend Repo Workflow

File:

- `.github/workflows/deploy-backend-azure.yml`

What it does:

- logs in to Azure with OIDC
- logs in to ACR
- builds and pushes the backend container image
- updates the backend Container App to the new image

## Frontend Repo Workflow

File:

- `.github/workflows/deploy-frontend-azure.yml`

What it does:

- logs in to Azure with OIDC
- reads the backend Container App FQDN
- builds the frontend image with `VITE_API_BASE_URL=https://<backend-fqdn>/api`
- pushes the frontend image
- updates the frontend Container App to the new image

## Required GitHub Secrets

Add these secrets to both repositories unless noted otherwise:

- `AZURE_CLIENT_ID`
- `AZURE_TENANT_ID`
- `AZURE_SUBSCRIPTION_ID`
- `AZURE_RESOURCE_GROUP`
- `AZURE_ACR_LOGIN_SERVER`
- `AZURE_ACR_USERNAME`
- `AZURE_ACR_PASSWORD`

Backend repo only:

- `AZURE_BACKEND_CONTAINER_APP_NAME`

Frontend repo:

- `AZURE_FRONTEND_CONTAINER_APP_NAME`
- `AZURE_BACKEND_CONTAINER_APP_NAME`

## Notes

- These workflows use GitHub OIDC for Azure login.
- They currently use ACR username/password for image push because that is the fastest path from the current dev/test setup.
- For production, move image pull and deployment permissions toward managed identities and tighter RBAC.
