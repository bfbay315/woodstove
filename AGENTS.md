# Repository Guidelines

## Project Structure & Module Organization
- `backend/`: Spring Boot service (Java 17). Core code lives in `backend/src/main/java/com/woodstove` with subpackages for `controller`, `service`, `repository`, `model`, and `dto`.
- `backend/src/main/resources`: configuration and Liquibase migrations in `db/changelog`.
- `frontend/`: SvelteKit app. Routes live in `frontend/src/routes`, shared utilities/components in `frontend/src/lib`.
- `docker-compose.yml`: local PostgreSQL stack for development.

## Build, Test, and Development Commands
- `docker-compose up -d`: start the local PostgreSQL database.
- `cd backend && ./gradlew bootRun`: run the API server on port 8080.
- `cd backend && ./gradlew test`: run backend unit/integration tests (JUnit Platform).
- `cd backend && ./gradlew build`: build the backend artifact.
- `cd frontend && npm install`: install frontend dependencies.
- `cd frontend && npm run dev`: run the SvelteKit dev server on port 5173.
- `cd frontend && npm run build`: build the frontend for production.
- `cd frontend && npm run preview`: preview the production build locally.
- `cd frontend && npm run check`: run Svelte/TypeScript checks.

## Coding Style & Naming Conventions
- Follow existing formatting in each module: Java files use standard 4-space indentation; Svelte/TypeScript files use tabs.
- Package and class naming follows Spring Boot conventions (e.g., `TemperatureService`, `TemperatureController`).
- Keep DTOs in `backend/src/main/java/com/woodstove/dto` and REST endpoints under `/api`.

## Testing Guidelines
- Backend tests run with JUnit Platform via Gradle (`./gradlew test`).
- No dedicated frontend test runner is configured; use `npm run check` for type/syntax validation.
- Name backend test classes with the standard `*Test` suffix and mirror package structure.

## Commit & Pull Request Guidelines
- Recent history uses short, descriptive commit messages (no enforced conventional format).
- Prefer imperative, concise summaries (e.g., “add test data”, “fix stats query”).
- For PRs, include: purpose/summary, any API or UI changes, and screenshots for UI updates.

## Configuration & Environment Tips
- Backend configs live in `backend/src/main/resources` (`application.properties`, `application-dev.properties`, `application-prod.properties`).
- Frontend environment hints are in `frontend/.env.example`.
- Database schema migrations are managed via Liquibase in `backend/src/main/resources/db/changelog`.
