# Azure Production Hardening

Use this checklist before promoting the current dev/test Azure deployment to production.

## Networking

- Move PostgreSQL Flexible Server to private networking.
- Restrict backend exposure behind a controlled entry point instead of leaving it broadly public.
- Keep frontend public, but protect it with Azure Front Door or another edge layer if you need WAF and better TLS control.

## Identity And Registry

- Stop using ACR admin credentials for long-term production operations.
- Configure Azure Container Apps to pull images from ACR with managed identity.
- Use least-privilege identities for deployment and runtime separately.

## Secrets

- Rotate the database password and JWT secret before production go-live.
- Store runtime secrets in Container Apps secrets and move toward Key Vault-backed secret management.
- Avoid build-time secrets in GitHub Actions.

## Data

- Enable PostgreSQL backups and validate restore procedures.
- Review compute sizing for PostgreSQL and Container Apps.
- Turn off development seed-data assumptions before production use.

## App Security

- Narrow backend `CORS_ALLOWED_ORIGINS` to only the production frontend hostname.
- Review JWT expiration and refresh token lifetime settings for production risk tolerance.
- Add monitoring and alerting for failed auth, backend errors, and database connectivity.

## GitHub Actions

- Use GitHub OIDC for Azure login instead of long-lived publish credentials.
- Protect `main` with pull requests and required checks.
- Scope repository secrets per repo and environment.

## Recommended Next Production Change

The most valuable next improvement for this app is:

1. make PostgreSQL private
2. switch image pulls to managed identity
3. place a custom domain in front of the frontend
4. rebuild frontend against the final API hostname
