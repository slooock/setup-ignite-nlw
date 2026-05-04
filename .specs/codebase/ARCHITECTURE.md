# Architecture

**Pattern:** Monorepo with decoupled applications

## High-Level Structure

This project is a monorepo containing three distinct applications:

- **`web`**: A frontend web application built with React and Vite.
- **`mobile`**: A mobile application for iOS and Android built with React Native and Expo.
- **`server`**: A backend API server built with Node.js, Fastify, and Prisma.

The frontend applications (`web` and `mobile`) are clients that consume the API provided by the `server`.

```
┌──────────┐      ┌──────────────┐      ┌──────────┐
│   Web    │      │    Server    │      │  Mobile  │
│ (React)  ├─────►│  (Fastify)   ├─────►│  (React  │
└──────────┘      │              │      │  Native) │
                  └──────┬───────┘      └──────────┘
                         │
                         ▼
                  ┌──────────┐
                  │ Database │
                  │ (SQLite) │
                  └──────────┘
```

## Identified Patterns

### API-Driven Communication

**Location:** `web/src/lib/axios.ts`, `mobile/src/lib/axios.ts`, `server/src/routes.ts`
**Purpose:** The frontend applications communicate with the backend exclusively through HTTP API calls. This decouples the client from the server logic.
**Implementation:**
- Both `web` and `mobile` apps use an `axios` instance to make requests to the backend.
- The `server` uses Fastify to define and handle API routes.

### Component-Based UI

**Location:** `web/src/components/`, `mobile/src/components/`
**Purpose:** The UI is broken down into small, reusable components.
**Implementation:** React components are defined in their own files and organized into a `components` directory in both the `web` and `mobile` projects.
**Example:** `web/src/components/Header.tsx`

### ORM for Data Access

**Location:** `server/src/lib/prisma.ts`, `server/prisma/schema.prisma`
**Purpose:** Prisma is used as an Object-Relational Mapper (ORM) to abstract database interactions.
**Implementation:**
- The database schema is defined in `prisma/schema.prisma`.
- A Prisma client instance is created in `lib/prisma.ts` and used in the application's routes to query the database.

## Data Flow

### Habit Creation and Tracking

1.  A user interacts with the UI in the `web` or `mobile` app to create a new habit.
2.  The client sends a `POST` request to the `/habits` endpoint on the `server`.
3.  The server's route handler validates the request data (using Zod).
4.  The handler uses the Prisma client to create a new habit record in the database.
5.  The user can then view their habits. The client sends a `GET` request to the `/summary` endpoint.
6.  The server queries the database for all habits and their completion status for the day and returns the data to the client.
7.  The client UI re-renders to display the habit summary.

## Code Organization

**Approach:** The code within each application is organized by layer/feature.
**Structure:**
- `src/components`: Reusable UI components.
- `src/screens` (mobile) / `src/pages` (web, implied): Top-level page/screen components.
- `src/routes`: Navigation and API route definitions.
- `src/lib`: Configuration and instantiation of libraries (e.g., Axios, Prisma, Day.js).
- `src/utils`: Shared utility functions.
