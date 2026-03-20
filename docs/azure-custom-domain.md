# Azure Custom Domain

This app is deployed on Azure Container Apps, so custom domains are attached at the Container App layer.

## Recommended Production Shape

- `app.yourdomain.com` -> frontend container app
- `api.yourdomain.com` -> backend container app

## Frontend Custom Domain With Managed Certificate

1. Get the frontend app FQDN:

```powershell
az containerapp show `
  --name pft-frontend-devtest `
  --resource-group rg-pft-devtest-centralindia `
  --query properties.configuration.ingress.fqdn `
  -o tsv
```

2. Add DNS records at your domain provider:

- CNAME: `app` -> `<frontend-fqdn>`
- TXT: `asuid.app` -> verification code from the Container App custom domain flow

3. Add and bind the hostname:

```powershell
az containerapp hostname add `
  --name pft-frontend-devtest `
  --resource-group rg-pft-devtest-centralindia `
  --hostname app.yourdomain.com

az containerapp hostname bind `
  --name pft-frontend-devtest `
  --resource-group rg-pft-devtest-centralindia `
  --environment pft-env-devtest `
  --hostname app.yourdomain.com `
  --validation-method CNAME
```

## Backend Custom Domain

Repeat the same flow for the backend app using:

- hostname: `api.yourdomain.com`
- container app: `pft-backend-devtest`

## Update Frontend API Target

If you move the backend from the default Azure hostname to `api.yourdomain.com`, rebuild and redeploy the frontend image with:

```text
VITE_API_BASE_URL=https://api.yourdomain.com/api
```

## Notes

- Every custom hostname needs TLS binding.
- Managed certificates are the simplest option for public Container Apps.
- Use separate frontend and backend hostnames so CORS and API routing stay explicit.
