# Personal Finance Tracker Backend

Spring Boot backend for a personal finance tracker web application built for:

- recording income and expenses quickly
- understanding spending trends
- managing monthly budgets
- tracking savings goals
- reviewing recurring payments

## Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- H2 for tests

## Features Included

- `transactions` API for income and expense records
- `budgets` API for monthly category budgets
- `savings-goals` API with contribution updates
- `recurring-payments` API for scheduled expenses
- `dashboard/summary` API for the finance overview screen
- JWT authentication with refresh token support
- forgot-password and reset-password flows
- CORS configuration for a React frontend running on `http://localhost:3000`
- development seed data for first-run testing

## Product Documentation

- Information architecture: `docs/information-architecture.md`

## Repository Split

- Backend repo: this folder, `d:\Workspace\personal-finance-tracker-backend`
- Frontend repo: `d:\Workspace\personal-finance-tracker-frontend`

## Database Configuration

The app reads PostgreSQL settings from environment variables and falls back to local defaults:

```properties
DB_URL=jdbc:postgresql://localhost:5432/personal_finance_tracker
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=your-base64-encoded-secret
```

Additional auth settings:

```properties
JWT_EXPIRATION_MS=3600000
JWT_REFRESH_EXPIRATION_MS=604800000
PASSWORD_RESET_EXPIRATION_MS=3600000
```

## Run

```bash
mvn spring-boot:run
```

Local PostgreSQL startup on this machine:

```powershell
powershell -ExecutionPolicy Bypass -File .\run\start-backend-postgres.local.ps1
```

Local PostgreSQL stop on this machine:

```powershell
powershell -ExecutionPolicy Bypass -File .\run\stop-backend-postgres.local.ps1
```

Quick restart flow on this machine:

```powershell
powershell -ExecutionPolicy Bypass -File .\run\stop-backend-postgres.local.ps1
powershell -ExecutionPolicy Bypass -File .\run\start-backend-postgres.local.ps1
```

Local deploy flow on this machine:

```powershell
powershell -ExecutionPolicy Bypass -File .\run\deploy-backend-postgres.local.ps1
```

This build-and-deploy script:

- stops the current backend on port `8080`
- packages the Spring Boot jar with Maven
- starts the packaged jar in the background with PostgreSQL settings
- writes logs to `run\deploy-backend.out.log` and `run\deploy-backend.err.log`

Full-stack deploy on this machine:

```powershell
powershell -ExecutionPolicy Bypass -File .\run\deploy-full-stack.local.ps1
```

This deploys the backend from this repo and the sibling frontend repo at `d:\Workspace\personal-finance-tracker-frontend`.

Full-stack Podman deploy on this machine:

```powershell
powershell -ExecutionPolicy Bypass -File .\run\deploy-podman.local.ps1
```

This script:

- creates a local Podman machine named `personal-finance-podman` if needed
- starts the machine when Podman is not already reachable
- refreshes `PATH` so a newly installed compose provider is picked up
- runs `podman compose -f .\compose.podman.yaml up --build -d`
- prints the frontend, backend, and database endpoints after startup

Frontend now lives in a separate project folder:

```bash
cd d:\Workspace\personal-finance-tracker-frontend
npm install
npm run dev
```

Recommended frontend env:

```properties
VITE_API_BASE_URL=http://localhost:8080/api
```

## Main Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`
- `GET /api/auth/me`
- `GET /api/dashboard/summary?month=2026-03`
- `GET /api/transactions?month=2026-03`
- `POST /api/transactions`
- `GET /api/budgets?month=2026-03`
- `POST /api/budgets`
- `GET /api/savings-goals`
- `POST /api/savings-goals`
- `PATCH /api/savings-goals/{goalId}/contributions`
- `GET /api/recurring-payments`
- `POST /api/recurring-payments`

Authentication request examples:

```bash
curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"alice@example.com\",\"password\":\"SecurePass1\",\"displayName\":\"Alice\"}"
```

```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"alice@example.com\",\"password\":\"SecurePass1\"}"
```

Passwords must:

- be at least 8 characters
- include uppercase and lowercase letters
- include at least one number

Example create transaction request:

```bash
curl -X POST http://localhost:8080/api/transactions ^
  -H "Content-Type: application/json" ^
  -d "{\"description\":\"Salary\",\"amount\":6200.00,\"transactionDate\":\"2026-03-01\",\"type\":\"INCOME\",\"category\":\"SALARY\",\"notes\":\"Monthly payroll\"}"
```

## Test

```bash
mvn test
```

## Podman

For a local full-stack Podman deployment guide, see `docs/podman-deploy.md`.
