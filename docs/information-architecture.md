# Personal Finance Tracker Information Architecture

## Main Navigation

### Dashboard
- Route: `/dashboard`
- Purpose: one-screen financial summary that gives the user a clear view of their current month and upcoming priorities.
- Widgets:
  - Current month income
  - Current month expenses
  - Net balance
  - Budget progress cards (per category)
  - Spending by category chart
  - Income vs expense trend chart
  - Recent transactions list
  - Upcoming recurring payments
  - Savings goal progress summary
- Actions:
  - Add transaction
  - View all transactions
  - Create budget
  - Add recurring bill
  - Update goal contribution
- Primary APIs:
  - `GET /api/dashboard/summary`

### Categories
- Purpose: organize transactions into meaningful buckets for reporting and filtering
- Default Categories
  - **Expense:** Food, Rent, Utilities, Transport, Entertainment, Shopping, Health, Education, Travel, Subscriptions, Miscellaneous
  - **Income:** Salary, Freelance, Bonus, Investment, Gift, Refund, Other
- Features:
  - Add custom category
  - Edit category name, icon, and color
  - Archive unused categories
  - Separate income and expense categories
- Primary APIs (planned):
  - `GET /api/categories`
  - `POST /api/categories`
  - `PUT /api/categories/{id}`
  - `DELETE /api/categories/{id}`

### Transactions
- Route: `/transactions`
- Purpose: full ledger for income, expenses, and transfers
- Transaction Fields:
  - `id`
  - `userId`
  - `accountId`
  - `type` (income, expense, transfer)
  - `amount`
  - `date`
  - `categoryId`
  - `note`
  - `merchant`
  - `paymentMethod`
  - `recurringTransactionId` (optional)
  - `tags`
  - `createdAt`
  - `updatedAt`
- Features:
  - Create transaction
  - Edit transaction
  - Delete transaction
  - Filter by date, category, amount, type, account
  - Search by merchant or note
  - Pagination / infinite scroll
  - Bulk delete / bulk categorize (optional V1.1)
- Edge Cases:
  - Prevent negative amount input
  - Support back-dated entries
  - Transfer transactions affect two accounts (debit/credit)
- Primary APIs:
  - `GET /api/transactions`
  - `POST /api/transactions`
  - `PUT /api/transactions/{id}`
  - `DELETE /api/transactions/{id}`

### Budgets
- Route: `/budgets`
- Purpose: monthly budget planning and monitoring
- Budget Fields:
  - `id`
  - `userId`
  - `categoryId`
  - `month`
  - `year`
  - `amount`
  - `alertThresholdPercent`
- Features:
  - Set monthly budget by category
  - View budget vs actual spend
  - Alert when 80%, 100%, 120% exceeded
  - Duplicate last month budget
- Primary content:
  - category budget cards
  - budget vs actual spending
  - remaining amount and over-budget warnings
  - create or adjust monthly budgets
- Primary APIs:
  - `GET /api/budgets`
  - `POST /api/budgets`

### Goals
- Route: `/goals`
- Purpose: savings goal creation and progress tracking
- Goal Fields:
  - `id`
  - `userId`
  - `name`
  - `targetAmount`
  - `currentAmount`
  - `targetDate`
  - `linkedAccountId` (optional)
  - `icon`
  - `color`
  - `status`
- Features:
  - Create savings goal
  - Add contribution
  - Withdraw from goal
  - Track progress
  - Mark goal completed
- Primary content:
  - active and achieved goals
  - target amount, current amount, completion percentage
  - contribute-to-goal action
  - target date visibility
- Primary APIs:
  - `GET /api/savings-goals`
  - `POST /api/savings-goals`
  - `PATCH /api/savings-goals/{goalId}/contributions`

### Reports
- Route: `/reports`
- Purpose: analytics and trends for financial health and budgeting
- Reports:
  - Monthly spending report
  - Category breakdown
  - Income vs expense trend
  - Account balance trend
  - Savings progress
- Filters:
  - Date range
  - Account
  - Category
  - Transaction type
- Export:
  - CSV export
  - PDF export (V1.1)
- Phase note:
  - no dedicated reports endpoint exists yet
  - initial version can compose data from dashboard, transactions, and budgets APIs

### Recurring Transactions
- Route: `/recurring`
- Purpose: manage subscriptions and repeating payments
- Fields:
  - `id`
  - `userId`
  - `title`
  - `type`
  - `amount`
  - `categoryId`
  - `accountId`
  - `frequency` (daily, weekly, monthly, yearly)
  - `startDate`
  - `endDate`
  - `nextRunDate`
  - `autoCreateTransaction` (bool)
- Features:
  - Create subscription or recurring salary
  - Show next due date
  - Auto-generate transaction with scheduled job
  - Pause or delete recurring item
- Primary APIs:
  - `GET /api/recurring-payments`
  - `POST /api/recurring-payments`
  - `PUT /api/recurring-payments/{id}`
  - `DELETE /api/recurring-payments/{id}`

### Accounts
- Route: `/accounts`
- Purpose: manage financial containers and view balances per account
- Account Types:
  - Bank account
  - Credit card
  - Cash wallet
  - Savings account
- Account Fields:
  - `id`
  - `userId`
  - `name`
  - `type`
  - `openingBalance`
  - `currentBalance`
  - `institutionName`
  - `lastUpdatedAt`
- Features:
  - Create account
  - View balance by account
  - Transfer funds between accounts
- Phase note:
  - backend support does not exist yet
  - recommended future API group: `/api/accounts`

### Settings
- Route: `/settings`
- Purpose: user preferences and profile management
- Primary content:
  - profile details
  - password change
  - notification preferences
  - currency, locale, and theme settings
  - security session management
- Current API support:
  - `GET /api/auth/me`
- Phase note:
  - profile update and preference endpoints still need backend support

## Design System

### Design Principles
- Clean, calm, finance-friendly visual style
- Emphasis on clarity over decoration
- Fast data entry with minimal friction
- Strong hierarchy for numbers and charts

### Visual Style
- Primary color: Deep blue or indigo
- Success color: Green
- Warning color: Amber
- Danger color: Red
- Background: Light neutral gray
- Cards: White with subtle shadow

### Typography
- Heading: Inter / system sans
- Large numbers for financial summaries
- Small muted labels for metadata

### Components
- App shell with sidebar + topbar
- Summary cards
- Data tables
- Modal form for add/edit transaction
- Charts
- Progress bars
- Tabs
- Toast notifications
- Empty states

## Secondary Navigation And Utilities

### Global Add Transaction Button
- Placement: persistent in app shell header and mobile bottom action
- Action:
  - opens add transaction modal or drawer
  - available from all authenticated routes except auth screens
- API:
  - `POST /api/transactions`

### Search
- Placement: header utility area
- Scope:
  - transactions first
  - later expand to goals, recurring payments, and settings
- UX:
  - global input with route-aware behavior
  - debounced client-side or server-side search

### Date Range Picker
- Placement: header utility area near search
- Scope:
  - dashboard
  - transactions
  - budgets
  - reports
- Default behavior:
  - current month on first load
  - shared filter state across overview pages where helpful

### Notifications
- Placement: header utility area
- Use cases:
  - upcoming recurring payments
  - overspent budgets
  - savings goal milestones
  - password reset and security notices
- Phase note:
  - backend notification center does not exist yet
  - initial version can be client-generated from existing API data

### User Profile Menu
- Placement: top-right header menu
- Menu items:
  - profile
  - settings
  - logout
- APIs:
  - `GET /api/auth/me`
  - `POST /api/auth/logout`

## Authentication Requirements

### Validation
- Email must be unique.
- Password must be at least **8 characters**.
- Password must include **upper/lowercase letters** and **a number**.

### Screens
- **Sign Up** (username, email, display name, password)
- **Login** (username/email + password)
- **Forgot Password** (email entry)
- **Reset Password** (token + new password)

## Nonfunctional Requirements

### Security
- JWT auth for API access.
- Password hashing with **bcrypt** (or Argon2) using strong cost factors.
- Rate limit login endpoints (e.g., 5 attempts per minute) to prevent brute force.
- Server-side validation for all financial inputs (amounts, dates, IDs).
### Security & Permissions
#### Access Model
- Single-user ownership of all records. All queries must be scoped by `userId` on backend.

#### Security Controls
- JWT access tokens
- Refresh tokens
- Hashed passwords
- HTTPS only
- Audit logs for key money-impacting actions (recommended)

## 19. Analytics / Telemetry

### Product Events
- `signup_completed`
- `first_transaction_added`
- `budget_created`
- `goal_created`
- `recurring_created`
- `report_exported`

### Notes
- Events should be emitted client-side for UI funnels and backend-side for security-sensitive events.
- Use a privacy-first analytics approach (no PII in telemetry payloads).

## 20. Milestones

### Milestone 1
- Auth + app shell
- Accounts + categories
- Transactions CRUD
- Basic dashboard

### Milestone 2
- Budgets
- Goals
- Recurring transactions

### Milestone 3
- Reports + exports
- Responsive polish
- Tests + deployment

## 21. Execution Plan

### Phase 0 – Setup & Alignment (1–2 days)
- Initialize repo structure (backend + frontend). 
- Configure linting, formatting, and CI (build + tests).
- Finalize API contract (OpenAPI/Swagger) and database schema.
- Define dev workflow (branching, PR review checklist).

### Phase 1 – Core Platform (2–3 weeks)
1. Implement auth (register/login/refresh/logout) + user model.
2. Build app shell (routing, app layout, protected routes).
3. Add Accounts + Categories (CRUD + user scoping).
4. Add Transactions CRUD + validations + the Add Transaction modal.
5. Implement basic dashboard summary endpoints & UI.

### Phase 2 – Finance Features (2–3 weeks)
1. Add Budgets (CRUD + spend aggregation).
2. Add Goals (create/contribute/withdraw + progress tracking).
3. Add Recurring transactions (CRUD + scheduler to create transactions).

### Phase 3 – Reporting & Polish (2–3 weeks)
1. Add Reports APIs + charts + export (CSV).
2. Implement responsive UI polish and improved UX.
3. Add notifications and toast alerts.

### Phase 4 – Stabilization & Release (1–2 weeks)
1. Add automated tests (unit + integration + e2e).
2. Harden security (rate limiting, audit logs, input validation).
3. Setup deployment pipeline (staging + prod).
4. Prepare launch checklist (monitoring, backups, docs).

### Notes
- Keep backend APIs stable; use feature flags for experimental UI.
- Track progress against milestones; adjust scope per sprint.

### Accessibility
- Keyboard navigable UI (focus states, logical tab order).
- Color contrast meets **WCAG AA** standards.
- Labels for form fields and chart summaries (aria-labels / aria-describedby).

### Responsiveness
- Desktop: full analytics layout with side navigation.
- Tablet: collapsed side nav + larger touch targets.
- Mobile: stacked cards + bottom action button.

## Route Map

### Public Routes
- `/login`
- `/sign-up`
- `/forgot-password`
- `/reset-password`

### Authenticated Routes
- `/dashboard`
- `/transactions`
- `/budgets`
- `/goals`
- `/reports`
- `/recurring`
- `/accounts`
- `/settings`

## Recommended React App Shell

### Desktop
- left sidebar for main navigation

## Sample Screenshots / Wireframes (Low-Fidelity)

### 9.1 Login Screen
+--------------------------------------------------+
|                Personal Finance Tracker          |
|--------------------------------------------------|
|  Welcome back                                    |
|  Email:    [______________________________]      |
|  Password: [______________________________]      |
|  [ Log In ]                                      |
|                                                  |
|  Forgot password?                                |
|  Don't have an account? Sign up                  |
+--------------------------------------------------+

### 9.2 Dashboard Screen
+--------------------------------------------------------------------------------+
| Logo | Dashboard | Transactions | Budgets | Goals | Reports | Search | Profile |
|--------------------------------------------------------------------------------|
| [Balance Card]   [Income Card]   [Expense Card]   [Savings Goal Card]          |
|--------------------------------------------------------------------------------|
| Spending by Category             | Income vs Expense Trend                     |
| [Pie/Donut Chart]                | [Line/Bar Chart]                            |
|--------------------------------------------------------------------------------|
| Recent Transactions              | Upcoming Bills                              |
| - Grocery    -$42                | - Netflix     Mar 20                        |
| - Salary    +$2400               | - Rent        Mar 25                        |
| - Fuel       -$18                | - Spotify     Mar 27                        |
+--------------------------------------------------------------------------------+

### 9.3 Transactions List
+--------------------------------------------------------------------------------+
| Transactions                                                   [Add Transaction]|
|--------------------------------------------------------------------------------|
| Filters: [Date] [Type] [Category] [Account] [Search__________]                 |
|--------------------------------------------------------------------------------|
| Date       | Merchant      | Category      | Account     | Type    | Amount    |
| 2026-03-01 | Grocery Mart  | Food          | HDFC Bank   | Expense | -42.00    |
| 2026-03-01 | Employer Inc  | Salary        | HDFC Bank   | Income  | +2400.00  |
| 2026-03-02 | Uber          | Transport     | Credit Card | Expense | -11.50    |
+--------------------------------------------------------------------------------+

### 9.4 Add Transaction Modal
+--------------------------------------------+
| Add Transaction                            |
|--------------------------------------------|
| Type:      (o) Expense ( ) Income ( ) Transfer
| Amount:    [____________________]          |
| Date:      [____/____/________]            |
| Account:   [Select v]                      |
| Category:  [Select v]                      |
| Merchant:  [____________________]          |
| Note:      [____________________]          |
| Tags:      [____________________]          |
|                                            |
|                 [Cancel] [Save]            |
+--------------------------------------------+

### 9.5 Budgets Screen
+----------------------------------------------------------------------------+
| Budgets                                                     [Set Budget]   |
|----------------------------------------------------------------------------|
| Food          650 / 800        [########----] 81%                          |
| Transport     120 / 250        [#####-------] 48%                          |
| Entertainment 210 / 200        [###########-] 105%                         |
| Shopping       75 / 300        [###---------] 25%                          |
+----------------------------------------------------------------------------+

### 9.6 Goals Screen
+----------------------------------------------------------------------------+
| Savings Goals                                                [Add Goal]    |
|----------------------------------------------------------------------------|
| Emergency Fund     45,000 / 100,000   [######------] 45%   Due: Dec 2026  |
| Vacation           20,000 / 50,000    [####--------] 40%   Due: Aug 2026  |
+----------------------------------------------------------------------------+

### 9.7 Reports Screen
+----------------------------------------------------------------------------+
| Reports                                                                     |
|----------------------------------------------------------------------------|
| Date Range: [This Month v]   Account: [All v]   Type: [All v]               |
|----------------------------------------------------------------------------|
| [Bar Chart: Category Spend]                                                 |
|----------------------------------------------------------------------------|
| [Line Chart: Income vs Expense by Month]                                    |
|----------------------------------------------------------------------------|
| Top Categories: Food, Rent, Transport                                       |
+----------------------------------------------------------------------------+

### 9.8 Sample UI Mockups

#### 9.8.1 Login / Sign Up
+--------------------------------------------------+
| [Logo]     Personal Finance Tracker              |
|--------------------------------------------------|
|  Email:    [______________________________]      |
|  Password: [______________________________]      |
|  [ Log In ]          [ Sign Up ]                 |
|                                                  |
|  Forgot password?                                |
+--------------------------------------------------+

#### 9.8.2 Dashboard (Desktop)
+--------------------------------------------------------------------------------+
| [Logo] | Dashboard | Transactions | Budgets | Goals | Reports | Search | User |
|--------------------------------------------------------------------------------|
| [Balance Card]   [Income Card]   [Expense Card]   [Savings Goal Card]          |
|--------------------------------------------------------------------------------|
| Spending by Category             | Income vs Expense Trend                     |
| [Pie/Donut Chart]                | [Line/Bar Chart]                            |
|--------------------------------------------------------------------------------|
| Recent Transactions              | Upcoming Bills                              |
| - Grocery    -$42                | - Netflix     Mar 20                        |
| - Salary    +$2400               | - Rent        Mar 25                        |
| - Fuel       -$18                | - Spotify     Mar 27                        |
+--------------------------------------------------------------------------------+

#### 9.8.3 Transaction Form (Modal)
+--------------------------------------------+
| Add Transaction                            |
|--------------------------------------------|
| Type:      (o) Expense ( ) Income ( ) Transfer
| Amount:    [____________________]          |
| Date:      [____/____/________]            |
| Account:   [Select v]                      |
| Category:  [Select v]                      |
| Merchant:  [____________________]          |
| Note:      [____________________]          |
| Tags:      [____________________]          |
|                                            |
|                 [Cancel] [Save]            |
+--------------------------------------------+

#### 9.8.4 Budget Card (List Item)
+--------------------------------------------------------------+
| Food          650 / 800        [########----] 81%               |
| Remaining: 150 | Last month: 720                            |
+--------------------------------------------------------------+

#### 9.8.5 Goal Card (List Item)
+--------------------------------------------------------------+
| Emergency Fund     45,000 / 100,000   [######------] 45%        |
| Target: Dec 2026  | Monthly contribution: 2,000               |
+--------------------------------------------------------------+

## 10. Detailed UI Flows

### 10.1 Onboarding Flow
1. User lands on marketing/login page.
2. User clicks **Sign Up**.
3. User enters email, password, name.
4. System validates input.
5. Account is created.
6. User is redirected to optional onboarding.
7. User creates first account (e.g., Bank Account).
8. User optionally sets first monthly budget.
9. User lands on dashboard.

### 10.2 Add Expense Flow
1. User clicks **Add Transaction**.
2. Modal opens with default type = **Expense**.
3. User enters amount, date, account, category, merchant.
4. User clicks **Save**.
5. Frontend validates required fields.
6. API stores transaction.
7. Account balance and dashboard widgets refresh.
8. Toast shown: “Transaction saved.”

### 10.3 Create Budget Flow
1. User navigates to **Budgets**.
2. User clicks **Set Budget**.
3. User chooses category, month, amount.
4. User saves budget.
5. Budget appears in list with progress bar.
6. Dashboard reflects updated budget summary.

### 10.4 Goal Contribution Flow
1. User opens **Goals**.
2. User selects an existing goal.
3. User clicks **Add Contribution**.
4. User enters amount and source account.
5. API creates transaction and updates goal balance.
6. Progress bar and account balance refresh.

### 10.5 Recurring Bill Flow
1. User opens **Recurring**.
2. User clicks **New Recurring Item**.
3. User enters title, amount, category, account, frequency.
4. User saves recurring item.
5. Scheduler sets nextRunDate.
6. Dashboard upcoming bills widget shows new item.

### 10.6 Reporting Flow
1. User opens **Reports**.
2. User selects date range and filters.
3. Frontend requests aggregated data from API.
4. Charts and tables update.
5. User optionally exports CSV.

- top header for secondary utilities
- content area with route outlet

### Mobile
- top bar with search and profile access
- bottom navigation for the highest-frequency destinations:
  - Dashboard
  - Transactions
  - Budgets
  - Goals
  - More
- floating action button for add transaction

## 11. API Specification

### 11.1 Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`

### 11.2 Transactions
- `GET /api/transactions`
- `POST /api/transactions`
- `GET /api/transactions/{id}`
- `PUT /api/transactions/{id}`
- `DELETE /api/transactions/{id}`

Sample Create Transaction Request
```
{
  "type": "expense",
  "amount": 42.50,
  "date": "2026-03-13",
  "accountId": "uuid",
  "categoryId": "uuid",
  "merchant": "Grocery Mart",
  "note": "Weekly groceries",
  "tags": ["family", "weekly"]
}
```

### 11.3 Categories
- `GET /api/categories`
- `POST /api/categories`
- `PUT /api/categories/{id}`
- `DELETE /api/categories/{id}`

### 11.4 Accounts
- `GET /api/accounts`
- `POST /api/accounts`
- `PUT /api/accounts/{id}`
- `POST /api/accounts/transfer`

### 11.5 Budgets
- `GET /api/budgets?month=3&year=2026`
- `POST /api/budgets`
- `PUT /api/budgets/{id}`
- `DELETE /api/budgets/{id}`

### 11.6 Goals
- `GET /api/goals`
- `POST /api/goals`
- `PUT /api/goals/{id}`
- `POST /api/goals/{id}/contribute`
- `POST /api/goals/{id}/withdraw`

### 11.7 Reports
- `GET /api/reports/category-spend`
- `GET /api/reports/income-vs-expense`
- `GET /api/reports/account-balance-trend`

### 11.8 Recurring
- `GET /api/recurring`
- `POST /api/recurring`
- `PUT /api/recurring/{id}`
- `DELETE /api/recurring/{id}`

## 12. Database Schema

### 12.1 Users
```sql
create table users (
  id uuid primary key,
  email varchar(255) unique not null,
  password_hash text not null,
  display_name varchar(120),
  created_at timestamp not null default now()
);
```

### 12.2 Accounts
```sql
create table accounts (
  id uuid primary key,
  user_id uuid not null references users(id),
  name varchar(100) not null,
  type varchar(30) not null,
  opening_balance numeric(12,2) not null default 0,
  current_balance numeric(12,2) not null default 0,
  institution_name varchar(120),
  created_at timestamp not null default now()
);
```

### 12.3 Categories
```sql
create table categories (
  id uuid primary key,
  user_id uuid references users(id),
  name varchar(100) not null,
  type varchar(20) not null,
  color varchar(20),
  icon varchar(50),
  is_archived boolean not null default false
);
```

### 12.4 Transactions
```sql
create table transactions (
  id uuid primary key,
  user_id uuid not null references users(id),
  account_id uuid not null references accounts(id),
  category_id uuid references categories(id),
  type varchar(20) not null,
  amount numeric(12,2) not null,
  transaction_date date not null,
  merchant varchar(200),
  note text,
  payment_method varchar(50),
  created_at timestamp not null default now(),
  updated_at timestamp not null default now()
);
```

### 12.5 Budgets
```sql
create table budgets (
  id uuid primary key,
  user_id uuid not null references users(id),
  category_id uuid not null references categories(id),
  month int not null,
  year int not null,
  amount numeric(12,2) not null,
  alert_threshold_percent int default 80
);
```

### 12.6 Goals
```sql
create table goals (
  id uuid primary key,
  user_id uuid not null references users(id),
  name varchar(120) not null,
  target_amount numeric(12,2) not null,
  current_amount numeric(12,2) not null default 0,
  target_date date,
  status varchar(30) not null default 'active'
);
```

### 12.7 Recurring Transactions
```sql
create table recurring_transactions (
  id uuid primary key,
  user_id uuid not null references users(id),
  title varchar(120) not null,
  type varchar(20) not null,
  amount numeric(12,2) not null,
  category_id uuid references categories(id),
  account_id uuid references accounts(id),
  frequency varchar(20) not null,
  start_date date not null,
  end_date date,
  next_run_date date not null,
  auto_create_transaction boolean not null default true
);
```

## Navigation Priority

### Primary Frequency
- Dashboard
- Transactions
- Budgets
- Goals

### Secondary Frequency
- Recurring
- Reports

### Lower Frequency
- Accounts
- Settings

## 13. Backend Architecture

### Layers
- **Controllers** (API entrypoints)
- **Application services** (business orchestration, use cases)
- **Domain models** (entities, value objects, domain rules)
- **Repository / ORM layer** (data access, persistence)
- **PostgreSQL** (relational storage)

### Suggested Structure
```
/backend
  /Controllers
  /Services
  /DTOs
  /Entities
  /Repositories
  /Migrations
```

### Cross-Cutting Concerns
- Logging
- Validation
- Authentication middleware
- Exception handling middleware
- Background job for recurring transaction generation

## Dependencies On Future Backend Work

- Reports module needs dedicated aggregation endpoints for richer charts.
- Accounts module needs account entities, balances, and account-linked transactions.
- Settings needs profile update, password change, and notification preference endpoints.
- Notifications need a real backend event or reminder model if they should persist across sessions.

## 14. Frontend Architecture

### Suggested React Structure
```
/frontend/src
  /components
  /pages
  /features
    /auth
    /transactions
    /budgets
    /goals
    /reports
  /services
  /hooks
  /store
  /types
  /utils
```

### Recommended Libraries
- React Router
- TanStack Query
- Zustand or Redux Toolkit
- React Hook Form
- Zod
- Recharts
- Axios

## 15. State Management Guidelines

### Server State
Use **TanStack Query** for server-sourced data:
- transactions
- budgets
- dashboard summary
- goals
- reports

### Local UI State
Use **Zustand** or component-local state for UI-only concerns:
- modal open/close
- active filters
- selected date range
- table sorting

## 16. Validation Rules

### Transaction Rules
- Amount required and greater than 0
- Date required
- Account required
- Category required except transfer
- Transfer requires source and destination account

### Budget Rules
- One budget per category per month per user
- Amount must be > 0

### Goal Rules
- Target amount must be > 0
- Contribution cannot exceed available balance if linked to account

## 17. Notifications and Alerts

### Examples
- Budget 80% used
- Budget exceeded
- Upcoming recurring payment in 3 days
- Goal reached
- Transaction saved successfully

### Notification Channels in V1
- In-app toast
- In-app alert banners

## 18. Error States and Empty States

### Empty States
- No transactions yet → show CTA to add first transaction
- No budgets yet → suggest budget creation
- No goals yet → suggest goal setup
- No report data → suggest expanding date range

### Error States
- API unavailable
- Unauthorized / session expired
- Validation error on form submit
- Failed chart/report fetch
