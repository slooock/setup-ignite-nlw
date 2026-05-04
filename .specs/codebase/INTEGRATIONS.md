# External Integrations

## API (Internal)

**Service:** Internal Backend API
**Purpose:** Provides data and functionality to the web and mobile clients.
**Implementation:**
- The `web` and `mobile` applications use `axios` to make HTTP requests to the server.
- The `server` uses Fastify to expose RESTful endpoints.
**Configuration:**
- **Web:** `baseURL` is hardcoded to `http://localhost:3333` in `web/src/lib/axios.ts`.
- **Mobile:** `baseURL` is hardcoded to `http://192.168.0.30:3333` in `mobile/src/lib/axios.ts`.
**Authentication:** No authentication mechanism is currently implemented.

## Database

**Service:** SQLite
**Purpose:** To persist application data for the `server`.
**Implementation:** The `server` uses the Prisma ORM to connect to and query the database.
**Configuration:** The database provider is defined in `server/prisma/schema.prisma`. The connection URL is expected to be in an environment variable (`DATABASE_URL`).

## Fonts

**Service:** Google Fonts
**Purpose:** To provide the "Inter" font to the mobile application.
**Implementation:** The `@expo-google-fonts/inter` package is used in `mobile/App.tsx` to load the font.

## Helper Libraries

### Day.js

**Purpose:** Used across all three applications (`web`, `mobile`, `server`) for consistent date and time manipulation.
**Location:** Imported and configured in `web/src/lib/dayjs.ts`, `mobile/src/lib/dayjs.ts`, and used directly in `server/src/routes.ts`.

### Zod

**Purpose:** Used in the `server` to validate the shape and type of incoming request bodies and parameters, ensuring data integrity before processing.
**Location:** Used within `server/src/routes.ts` to define schemas for each endpoint.

### CORS

**Service:** @fastify/cors
**Purpose:** To enable and configure Cross-Origin Resource Sharing on the `server`, allowing the `web` frontend to make requests from a different origin.
**Implementation:** The `cors` plugin is registered with the Fastify instance in `server/src/server.ts`.
