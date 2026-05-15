![CI](https://github.com/stefan-popovic-dev/JobApplicationTracker/actions/workflows/ci.yml/badge.svg)

# Job Application Tracker

A portfolio-grade REST API for tracking job applications, built to demonstrate production-ready Spring Boot 4 patterns: JWT authentication, user-scoped data access, JPA persistence, Testcontainers integration testing, GitHub Actions CI, and OpenAI-powered skill gap analysis. The target audience is hiring managers reviewing backend Java work.

---

## Tech Stack

| Concern | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Build | Gradle (Groovy DSL) |
| Database | PostgreSQL (via Docker Compose — auto-started) |
| ORM | Spring Data JPA + Hibernate |
| Auth | Spring Security 7 + custom JWT |
| HTTP client | RestClient |
| API docs | Springdoc OpenAPI (Swagger UI) |
| Testing | JUnit 5 + Testcontainers |
| AI | OpenAI API (gpt-4o-mini) |
| Container | Docker + Docker Compose |
| CI | GitHub Actions |

---

## Architecture Decisions

- **UUID primary keys** — avoids enumeration attacks where sequential integer IDs let an authenticated user guess valid resource IDs belonging to other users. UUIDs are also safe to expose in URLs without leaking record counts.

- **DTOs instead of exposing entities** — JPA entities carry ORM metadata, lazy-loaded collections, and internal fields that should never cross the API boundary. Separate request/response records give explicit control over what enters and leaves the system, and decouple the persistence model from the API contract.

- **User-scoped data access** — every read and write operation validates that the resource belongs to the authenticated user before executing. This is enforced at the service layer, not just the controller, so the rule holds regardless of how the service is called.

- **gpt-4o-mini for AI analysis** — cost-effective for text analysis tasks that do not require the reasoning depth of larger models. Skill gap analysis is a classification and summarisation task; gpt-4o-mini handles it well at a fraction of the cost.

- **Testcontainers over mocking the database** — mock repositories pass tests that fail in production when query behaviour, constraint violations, or transaction semantics differ between the mock and a real Postgres instance. Testcontainers spins up an actual Postgres container per test run, so the tests prove the SQL works.

---

## Prerequisites

- Java 21
- Docker Desktop (must be running before `bootRun` or `test`)
- An OpenAI API key

---

## Running Locally

```bash
# 1. Clone the repository
git clone https://github.com/stefan-popovic-dev/JobApplicationTracker.git
cd JobApplicationTracker

# 2. Set your OpenAI API key
# Windows (PowerShell)
$env:OPENAI_API_KEY = "sk-..."
# macOS / Linux
export OPENAI_API_KEY=sk-...

# 3. Start the application
# Spring Boot automatically starts the PostgreSQL container via Docker Compose
./gradlew bootRun

# 4. Open Swagger UI
# http://localhost:8080/swagger-ui.html
```

Use the Swagger UI to register a user, log in to get a JWT token, then click **Authorize** and paste the token to test authenticated endpoints.

---

## Running Tests

```bash
./gradlew test
```

Testcontainers launches a real PostgreSQL container during the test run. Docker Desktop must be running.

Test reports are written to `build/reports/tests/test/index.html`.

---

## API Overview

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | No | Register a new user account |
| POST | `/auth/login` | No | Authenticate and receive a JWT token |
| GET | `/applications` | Yes | List all job applications for the authenticated user |
| POST | `/applications` | Yes | Create a new job application |
| GET | `/applications/{id}` | Yes | Get a job application by ID |
| PUT | `/applications/{id}` | Yes | Update an existing job application |
| DELETE | `/applications/{id}` | Yes | Delete a job application |
| GET | `/applications/{appId}/contacts` | Yes | List all contacts for a job application |
| POST | `/applications/{appId}/contacts` | Yes | Add a contact to a job application |
| DELETE | `/applications/{appId}/contacts/{contactId}` | Yes | Delete a contact from a job application |
| POST | `/ai/analyze` | Yes | Analyze skill gaps between a resume and a job description |

All authenticated endpoints require a `Bearer <token>` header. Tokens are obtained from `/auth/login` or `/auth/register`.

---

## Environment Variables

| Variable | Required | Default | Description |
|---|---|---|---|
| `OPENAI_API_KEY` | Yes* | `placeholder` | OpenAI API key for the AI gap analysis endpoint |
| `JWT_SECRET` | No | `dev-secret-minimum-32-characters-long-padding` | HMAC secret for signing JWTs — override in production |

\* The application starts without a real key but the `/ai/analyze` endpoint will fail. All other endpoints function normally.
