[README.md](https://github.com/user-attachments/files/33331477/README.md)
# Chess Coach Dashboard

![CI](https://github.com/BondreaStefan/chess-dashboard/actions/workflows/ci.yml/badge.svg)

A web dashboard for chess coaches to manage their students, import their games and track their progress.

Coaches upload games as PGN — single games, multi-game exports from Lichess or Chess.com, or `.pgn` files — and the application parses them, works out which side the student played, and builds per-student statistics.

> **Status:** work in progress. The backend API is functional and tested; the React frontend is under active development.

---

## Features

**Implemented**

- Coach accounts with JWT authentication and two roles: `ADMIN` and `COACH`
- Ownership-based authorization: each coach sees and manages only their own students and games
- Student management, including Lichess and Chess.com usernames
- PGN import: single game, multi-game batch, or `.pgn` file upload
- Automatic detection of the student's colour by matching their online usernames against the PGN players
- Metadata extraction: result, date and time, ratings, ECO code, time control, move count
- Paginated game history per student
- Per-student statistics: results overall and by colour, with win rates
- Frontend: login, protected routes, student list

**Planned**

- Game viewer with move-by-move navigation
- Stockfish analysis: accuracy, mistakes and blunders, evaluation graph
- Direct import from the Lichess and Chess.com APIs, with deduplication
- Opening statistics, with ECO codes computed from the moves
- Results over time

---

## Tech stack

| Layer | Technologies |
|---|---|
| Backend | Java 21, Spring Boot 4.1, Spring Data JPA, Spring Security, Bean Validation |
| Database | PostgreSQL 17, Flyway migrations |
| Auth | JWT (jjwt), BCrypt |
| Chess | [chesslib](https://github.com/bhlangonijr/chesslib) for PGN parsing |
| Testing | JUnit 5, Mockito, AssertJ, MockMvc, Testcontainers |
| Frontend | React, TypeScript, Vite, TanStack Query, React Router |
| Tooling | Maven, Docker Compose, GitHub Actions |

---

## Getting started

### Prerequisites

- Java 21
- Docker (for the database and for Testcontainers)
- Node.js LTS (for the frontend)

### 1. Configure the environment

```bash
cp .env.example .env
```

Then edit `.env`:

| Variable | Purpose |
|---|---|
| `DB_PASSWORD` | Password for the local PostgreSQL container |
| `JWT_SECRET` | Signing key for tokens — at least 32 characters, randomly generated |
| `ADMIN_EMAIL` | Email of the admin account created on first startup |
| `ADMIN_PASSWORD` | Password of that admin account |

The same `.env` file is read by both Docker Compose and Spring Boot. It is git-ignored and must never be committed.

### 2. Start the database

```bash
docker compose up -d
```

PostgreSQL is exposed on port `5433` to avoid clashing with a local installation.

### 3. Start the backend

```bash
./mvnw spring-boot:run
```

Flyway applies the migrations on startup. On first run, an admin account is created from `ADMIN_EMAIL` and `ADMIN_PASSWORD`.

The API is available at `http://localhost:8080/api/v1`.

### 4. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. The Vite dev server proxies `/api` requests to the backend, so no CORS configuration is needed in development.

### 5. Log in

Use the admin credentials from `.env`, or register a coach account through `POST /api/v1/auth/register`.

---

## Running the tests

```bash
./mvnw verify
```

Docker must be running: repository and integration tests start a real PostgreSQL container through Testcontainers.

The test suite runs on every push and pull request via GitHub Actions.

---

## API overview

All endpoints are prefixed with `/api/v1`. Errors are returned as [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) Problem Details.

| Method | Path | Access | Description |
|---|---|---|---|
| `POST` | `/auth/register` | public | Register a coach account |
| `POST` | `/auth/login` | public | Obtain a JWT |
| `GET` / `PUT` | `/coaches/me` | authenticated | Read or update own profile |
| `GET` | `/coaches` | admin | List coaches |
| `GET` / `PUT` / `DELETE` | `/coaches/{id}` | admin | Manage a coach |
| `GET` / `POST` | `/students` | authenticated | List own students / create one |
| `GET` / `PUT` / `DELETE` | `/students/{id}` | owner | Manage a student |
| `PUT` | `/students/{id}/coach` | admin | Reassign a student to another coach |
| `POST` | `/games` | owner | Upload a single PGN game |
| `POST` | `/games/import` | owner | Import multiple games from one PGN |
| `POST` | `/games/import/file` | owner | Upload a `.pgn` file (multipart) |
| `GET` | `/games?studentId=` | owner | Paginated game history (`page`, `size`, `sort`) |
| `GET` / `DELETE` | `/games/{id}` | owner | Read or delete a game |
| `GET` | `/stats?studentId=` | owner | Student statistics |

Ready-to-run requests are available in the `.http` files in the repository root (VS Code REST Client).

---

## Architecture and design decisions

**Package by feature, with boundaries enforced by the compiler.**
The backend is organised into `coach`, `student`, `game` and `auth` packages. Entities, repositories and mappers are package-private; only services, controllers and DTOs are public. A package can reach another only through its service, so persistence details never leak across features.

**Ownership checks return 404, not 403.**
A coach requesting another coach's student receives `404 Not Found`. A resource that does not belong to you does not exist for you, so its existence is never confirmed.

**Role checks are declarative; ownership checks live in services.**
Admin-only endpoints use `@PreAuthorize`, applied at class level where possible so that new endpoints are secure by default. Ownership depends on data, so it is enforced in the service layer through filtered queries.

**Stateless JWT authentication.**
Tokens carry the coach's id, email and role, so authorization needs no database lookup per request. Unauthenticated requests receive `401`, unauthorized ones `403`, both as Problem Details.

**Flyway owns the schema.**
Hibernate runs with `ddl-auto=validate`: it checks that entities match the schema but never changes it. Every schema change is a versioned migration.

**The raw PGN is the source of truth.**
Each game stores its full PGN alongside the extracted metadata, so games can be reprocessed if the parser improves.

**The parser is isolated.**
All use of chesslib is confined to a single class that turns PGN text into a plain record. The rest of the application does not depend on the library.

**The student's colour is deduced, with an explicit fallback.**
For single uploads, the colour can be given explicitly. For batch imports, games in which the student does not appear are skipped rather than rejected, so a whole tournament file can be uploaded.

**Secrets fail fast.**
`JWT_SECRET` and `DB_PASSWORD` have no defaults: if they are missing, the application refuses to start instead of running with a weak key.

---

## Known limitations

- **PGN is normalised on import.** Stored PGN is re-serialised by chesslib: tag order may change, and the `Termination` tag is not preserved.
- **Chess.com exports have no ECO codes**, so opening statistics are not yet available for those games.
- **A batch import is all-or-nothing on parse errors.** One malformed game rejects the whole file, because chesslib parses the input as a unit.
- **Student emails are globally unique**, so the same student cannot belong to two coaches.
- **Tokens are stored in `localStorage`**, a common trade-off for an MVP; an `HttpOnly` cookie would be more resistant to XSS.
- **No token refresh.** Tokens expire after one hour and the user logs in again.

---

## Project structure

```
chess-dashboard/
├── src/main/java/.../chess_dashboard/
│   ├── auth/       authentication, JWT, security filter
│   ├── coach/      coach accounts and admin bootstrap
│   ├── student/    student management
│   ├── game/       PGN parsing, games, statistics
│   └── common/     configuration, exception handling
├── src/main/resources/db/migration/   Flyway migrations
├── src/test/                          unit, web and repository tests
├── frontend/                          React application
├── compose.yaml                       local PostgreSQL
└── .github/workflows/ci.yml           CI pipeline
```
