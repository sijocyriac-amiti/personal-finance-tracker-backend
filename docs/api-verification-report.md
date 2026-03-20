# API Verification Report

## Environment

- Backend repo: `d:\Workspace\personal-finance-tracker-backend`
- Local base URL: `http://localhost:8081`
- Runtime mode: Spring Boot with local H2-backed deployment
- Verification date: `2026-03-18`

## Authentication Verification

### Verified
- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/refresh`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`
- `POST /api/auth/logout`

### Demo Credentials
- Email: `demo@example.com`
- Password: `DemoPass1`

## Read API Verification

### Verified
- `GET /api/dashboard/summary?month=2026-03`
- `GET /api/accounts`
- `GET /api/categories`
- `GET /api/transactions?month=2026-03`
- `GET /api/budgets?month=2026-03`
- `GET /api/savings-goals`
- `GET /api/recurring-payments`

### Seed Data Confirmed
- Account: `Primary Checking`
- Categories: `SALARY`, `FOOD`, `RENT`
- Transactions: 3 seeded records
- Budgets: `FOOD`, `ENTERTAINMENT`
- Goal: `Emergency Fund`
- Recurring payment: `Netflix`

## Write API Verification

### Verified
- `POST /api/categories`
- `POST /api/accounts`
- `POST /api/transactions`
- `POST /api/budgets`
- `POST /api/savings-goals`
- `PATCH /api/savings-goals/{id}/contributions`
- `POST /api/recurring-payments`

### Data Created During Verification
- Category created:
  - `id=4`
  - `name=COFFEE`
- Account created:
  - `id=2`
  - `name=Cash Wallet`
- Transaction created:
  - `id=4`
  - `description=Coffee run`
- Budget created:
  - `id=3`
  - `category=SHOPPING`
- Goal created:
  - `id=2`
  - `name=Vacation Fund`
- Goal contribution verified:
  - `goalId=2`
  - amount moved from `5000.00` to `6500.00`
- Recurring payment created:
  - `id=2`
  - `title=Spotify`

## Result

### Status
- Local backend is working for both authenticated reads and writes.
- API verification passed for the primary V1 finance flows.

## Notes

- The backend is running on `8081` because `8080` was already occupied on this machine.
- Local verification was completed against the deployed backend instance, not just unit tests.
