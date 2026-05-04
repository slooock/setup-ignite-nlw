# Project Structure

**Root:** /Users/kayque/Documents/eagle/setup-ignite-nlw

## Directory Tree

```
/Users/kayque/Documents/eagle/setup-ignite-nlw/
├───mobile/
│   ├───app.json
│   ├───App.tsx
│   ├───package.json
│   └───src/
├───server/
│   ├───package.json
│   ├───prisma/
│   │   └───schema.prisma
│   └───src/
│       ├───routes.ts
│       └───server.ts
└───web/
    ├───package.json
    ├───vite.config.ts
    └───src/
        ├───App.tsx
        └───main.tsx
```

## Module Organization

### mobile

**Purpose:** Handles the mobile application (React Native).
**Location:** `/mobile`
**Key files:** `App.tsx`, `app.json`, `src/`

### server

**Purpose:** The backend server application.
**Location:** `/server`
**Key files:** `src/server.ts`, `prisma/schema.prisma`

### web

**Purpose:** The web frontend application.
**Location:** `/web`
**Key files:** `src/App.tsx`, `vite.config.ts`

## Where Things Live

**Mobile App:**

- UI/Interface: `mobile/src/screens/`, `mobile/src/components/`
- Business Logic: `mobile/src/screens/`, `mobile/src/utils/`
- Configuration: `mobile/app.json`, `mobile/babel.config.js`
- Routes: `mobile/src/routes/`

**Backend Server:**

- API Logic: `server/src/routes.ts`
- Data Access: `server/prisma/`, `server/src/lib/prisma.ts`
- Configuration: `server/package.json`
- Server Entrypoint: `server/src/server.ts`

**Web App:**

- UI/Interface: `web/src/components/`, `web/src/App.tsx`
- Business Logic: `web/src/utils/`
- Configuration: `web/vite.config.ts`, `web/tailwind.config.cjs`
- Entrypoint: `web/src/main.tsx`

## Special Directories

**prisma:**
**Purpose:** Contains database schema, migrations, and seed scripts for the server.
**Examples:** `schema.prisma`, `migrations/`
