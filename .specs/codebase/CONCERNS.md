# Codebase Concerns

**Analysis Date:** 2024-05-21

## Security Considerations

**API Authentication:**

- **Risk:** The API has no authentication or authorization mechanism. Any user with network access can read, create, and modify data, leading to potential data corruption or leakage.
- **Files:** `server/src/routes.ts`
- **Current mitigation:** None.
- **Recommendations:** Implement an authentication strategy (e.g., JWT, OAuth 2.0) to secure all endpoints. Add authorization logic to ensure users can only access their own data.

**Raw SQL Query:**

- **Risk:** The `/summary` endpoint uses a raw SQL query (`prisma.$queryRaw`). While currently safe as it doesn't use user input, raw queries are a common source of SQL injection vulnerabilities if modified incorrectly in the future. They also bypass the type safety and abstractions provided by the Prisma client.
- **Files:** `server/src/routes.ts`
- **Current mitigation:** The current query is static.
- **Recommendations:** Refactor the query to use the Prisma client's fluent API (e.g., `prisma.day.findMany` with `_count`) to ensure type safety and prevent potential vulnerabilities.

## Tech Debt

**Hardcoded Configuration:**

- **Issue:** API base URLs are hardcoded in both client applications. The mobile client uses a local IP address, which will not work for other developers or in different environments.
- **Files:**
    - `web/src/lib/axios.ts`
    - `mobile/src/lib/axios.ts`
- **Impact:** Makes it difficult to switch between development, staging, and production environments without code changes. It also complicates the setup for new developers.
- **Fix approach:** Use environment variables (e.g., via `.env` files and `process.env`) to manage configuration for different environments.

## Test Coverage Gaps

**No Automated Tests:**

- **What's not tested:** The entire codebase. There are no unit, integration, or end-to-end tests for the web, mobile, or server applications.
- **Risk:** High. Changes can introduce regressions that go unnoticed, leading to bugs in production. The lack of tests makes refactoring and adding new features risky and slow.
- **Priority:** High.
- **Difficulty to test:** Medium. The decoupled architecture makes it possible to test each part in isolation, but setting up the initial testing infrastructure will require effort.
- **Recommendations:**
    - **Server:** Introduce a testing framework like Vitest or Jest. Write unit tests for business logic and integration tests for API endpoints that interact with a test database.
    - **Web/Mobile:** Introduce React Testing Library and Jest/Vitest to write unit and integration tests for components. Implement end-to-end tests with a framework like Cypress (for web) or Detox/Maestro (for mobile).

## Scaling Limits

**Development Database:**

- **Resource:** SQLite Database.
- **Limit:** SQLite is a file-based database and is not suitable for production environments with concurrent users, as it can lead to locking issues and data corruption.
- **Files:** `server/prisma/dev.db`, `server/prisma/schema.prisma`
- **Symptoms at limit:** Write contention, poor performance under load.
- **Scaling path:** Configure Prisma to use a more robust database for production, such as PostgreSQL or MySQL.
