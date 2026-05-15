# Job Application Tracker — Claude Code Instructions

## Project purpose
A portfolio-grade REST API for tracking job applications with AI-powered gap analysis.
Built to demonstrate: Spring Boot 4, JWT auth, JPA, Docker, Testcontainers, CI/CD, and OpenAI integration.
The target audience for this codebase is hiring managers. Code clarity and commit history matter.

---

## Build, run, and test commands

```bash
# Run the application (starts Docker Compose + PostgreSQL automatically)
./gradlew bootRun

# Build and compile only
./gradlew build

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "dev.popovic.stefan.jobapplicationtracker.SomeTest"

# Check for compilation errors without running
./gradlew compileJava

# Clean build artifacts
./gradlew clean build
```

YOU MUST run `./gradlew build` after every implementation task and fix all errors before committing.
YOU MUST use `./gradlew` not `./mvnw` — this project uses Gradle, not Maven.

---

## Project structure

```
src/
  main/
    java/dev/popovic/stefan/jobapplicationtracker/
      config/          # SecurityConfig, OpenApiConfig, JwtConfig
      controller/      # REST controllers (Auth, Application, Contact, AI)
      dto/             # Request and response records — never expose entities directly
      entity/          # JPA entities (AppUser, JobApplication, Contact)
      repository/      # Spring Data JPA repositories
      security/        # JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
      service/         # Business logic layer
    resources/
      application.yml  # App config
  test/
    java/.../          # Integration tests using Testcontainers
compose.yaml           # Docker Compose (auto-managed by Spring Boot)
build.gradle           # Gradle build file
CLAUDE.md              # This file
```

---

## Code rules

**Entities**
- Use `AppUser` not `User` — `User` is a reserved word in PostgreSQL
- ALL primary keys are UUID using `@UuidGenerator` — never Long or auto-increment
- Entities use Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@Builder`
- Never expose JPA entities in API responses — always map to a DTO

**DTOs**
- Use Java records for all DTOs: `public record ApplicationResponse(UUID id, String company, ...)`
- Request DTOs use `@Valid` and Jakarta validation annotations (`@NotBlank`, `@Email`, etc.)
- Separate request and response types — never reuse the same record for both

**Controllers**
- All endpoints except `/auth/**` require authentication
- IMPORTANT: All data is user-scoped — a logged-in user can ONLY read and modify their own data
- Inject the authenticated user via `@AuthenticationPrincipal UserDetails userDetails`
- Return `ResponseEntity<T>` with explicit HTTP status codes

**Services**
- Business logic lives in `@Service` classes — controllers are thin
- Services validate ownership before any read or write operation
- Throw `ResponseStatusException` for 404, 403 errors — never return null

**Logging**
- Use `@Slf4j` from Lombok — NEVER use `System.out.println`
- Log at DEBUG for normal flow, ERROR for caught exceptions

**Security**
- BCrypt for all password hashing — never store plaintext
- JWT secret comes from `application.yml` via `${JWT_SECRET:dev-secret-change-in-prod}`
- OpenAI API key comes from `${OPENAI_API_KEY:placeholder}`

---

## Git rules

IMPORTANT: NEVER commit directly to `main` or `dev`.

### Branch model
```
main        ← clean, always runnable — what hiring managers review
└── dev     ← integration branch
    └── feat/<short-name>    ← one branch per build layer
    └── fix/<short-name>     ← for bug fixes
```

### Branch naming
```
feat/domain-model
feat/jwt-auth
feat/application-crud
feat/contact-resource
feat/ai-endpoint
feat/openapi-config
feat/testcontainers
feat/github-actions
fix/jwt-expiry-handling
```

### Before starting any task
```bash
git checkout dev
git pull origin dev
git checkout -b feat/<name>
```

### Commit message format — Conventional Commits
```
feat: add JobApplication entity and JPA repository
fix: correct user ownership check in ApplicationService
test: add Testcontainers integration test for auth flow
chore: add testcontainers dependency to build.gradle
docs: add README setup and environment variable guide
refactor: extract JWT validation logic into JwtUtil
```

Rules:
- Lowercase after the colon
- Present tense imperative ("add", not "added" or "adds")
- One logical change per commit — do not bundle unrelated changes
- `./gradlew build` MUST pass before committing

### Staging and committing
After `./gradlew build` passes:
```bash
git add -p          # Stage changes interactively — review each hunk
git status          # Verify only intended files are staged
git commit -m "feat: <message>"
```

NEVER run `git push` — stage and commit only. The developer pushes and opens the PR.
NEVER use `git add .` without first reviewing `git status`.

---

## Guardrails — do not touch without being explicitly asked

- `compose.yaml` — auto-managed by Spring Boot Docker Compose support
- `build.gradle` — only modify to add specific dependencies when requested
- `application.yml` — do not change datasource or port settings
- `.gitignore` — do not modify
- `main` branch — never commit here
- `dev` branch — never commit here directly

---

## Build sequence and current state

Complete layers in this order. Do not begin a layer until the previous one compiles and runs.

- [x] Layer 1 — Scaffold + Docker Compose (`./gradlew bootRun` works ✅)
- [ ] Layer 2 — Domain model (entities + repositories)
- [ ] Layer 3 — JWT auth (JwtUtil, JwtAuthFilter, SecurityConfig, AuthController)
- [ ] Layer 4 — Application CRUD (user-scoped)
- [ ] Layer 5 — Contact sub-resource
- [ ] Layer 6 — AI gap analysis endpoint (OpenAI via RestClient)
- [ ] Layer 7 — OpenAPI / Swagger UI config
- [ ] Layer 8 — Testcontainers integration test
- [ ] Layer 9 — GitHub Actions CI workflow
- [ ] Layer 10 — README

---

## What "done" means for each layer

A layer is done when:
1. `./gradlew build` passes with no errors or warnings
2. The feature works end-to-end (test with curl or Swagger UI)
3. Only files relevant to the layer are staged
4. Commit message follows Conventional Commits format
5. No debug code, no TODO comments left in staged files

---

## Tech stack reference

| Concern | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Build | Gradle (Groovy DSL) |
| Database | PostgreSQL (via Docker Compose — auto-started) |
| ORM | Spring Data JPA + Hibernate |
| Auth | Spring Security 7 + custom JWT |
| HTTP client | RestClient — not RestTemplate, not WebClient |
| API docs | Springdoc OpenAPI (Swagger UI at /swagger-ui.html) |
| Testing | JUnit 5 + Testcontainers + Spring Boot Test |
| AI | OpenAI API (gpt-4o-mini) via RestClient |
| Container | Docker + Docker Compose |
| CI | GitHub Actions |