# OpsPilot Repository Guidance

## Project Identity

- **Name:** OpsPilot
- **Tagline:** From Incident Detection to Intelligent Resolution.
- **Repository:** `AbdouTrdFer/OpsPilot-AI-Powered-Incident-Intelligence-Resolution-Platform`
- **Product type:** AI-assisted incident intelligence and resolution platform.
- **Primary users:** DevOps engineers, SREs, software engineers, and engineering managers.
- **Portfolio objective:** Demonstrate production-oriented Java/backend engineering, Oracle Database, AI/RAG, DevOps, observability, testing, and cloud skills for software-engineering and Oracle PFE opportunities.

OpsPilot must be developed as a credible engineering product, not as a collection of fashionable technologies and not as an incident CRUD application with a chatbot added afterward.

## Product Problem

When a production service fails, engineers must correlate alerts, metrics, logs, deployments, past incidents, and runbooks. That investigation is slow and fragmented.

OpsPilot should help answer:

1. What is failing?
2. When did the problem begin?
3. What evidence is relevant?
4. Has a semantically similar incident happened before?
5. Which runbook applies?
6. What is the most likely root-cause hypothesis?
7. What should an engineer investigate next?

The product goal is to reduce Mean Time to Detect and Mean Time to Resolve while keeping a human engineer responsible for operational decisions.

## Core Product Flow

```text
Telemetry or report
        -> incident detection/creation
        -> evidence correlation
        -> similar-incident retrieval
        -> runbook retrieval
        -> AI-supported root-cause hypothesis
        -> recommended investigation steps
        -> human validation
        -> resolution and captured learning
```

AI output must be presented as a hypothesis, not guaranteed truth. Show confidence, supporting evidence, related incidents, and retrieved runbook sources whenever possible.

## Current Repository State

The repository is in its bootstrap/documentation stage. The README contains the product description and a target architecture poster. Do not claim that planned components are already implemented. Inspect the repository before every task and treat the codebase as the source of truth.

## Architecture Strategy

The architecture poster represents the long-term direction, not the first implementation.

### MVP architecture

Begin with a modular monolith:

```text
Next.js + TypeScript dashboard
            |
            v
Java 17 + Spring Boot application
  - identity/access module
  - service catalog module
  - incident module
  - runbook module
  - analysis orchestration module
            |
            v
Oracle Database
```

Use clear module boundaries so a module can be extracted later, but do not create distributed services without a demonstrated need.

### Planned evolution

1. **Phase 1 — Core platform:** services, incidents, runbooks, incident lifecycle, RBAC, REST API, dashboard, database migrations, tests, Docker Compose, and CI.
2. **Phase 2 — Retrieval intelligence:** incident embeddings, similarity search, runbook retrieval, RAG, evidence-backed recommendations, and AI audit records.
3. **Phase 3 — Detection:** telemetry ingestion, rule-based detection first, then a separate Python anomaly-detection service if justified.
4. **Phase 4 — Observability:** OpenTelemetry instrumentation and collector, Prometheus metrics, Grafana dashboards, and explicit log/trace backends.
5. **Phase 5 — Cloud:** container deployment on OCI, managed Oracle Database, secrets management, and Terraform.
6. **Phase 6 — Agentic assistance:** a constrained investigation agent that calls approved search, metric, log, incident, and runbook tools. No autonomous remediation in the initial versions.

### Extraction rule

Only extract a microservice when at least one condition is true:

- it needs independent scaling;
- it has a distinct runtime or dependency boundary, such as Python ML;
- it requires independent deployment or failure isolation;
- its domain boundary is stable and supported by working code.

Record important architecture changes in `docs/adr/`.

## Initial Technology Direction

- **Backend:** Java 17 baseline, Spring Boot, Spring Security, Bean Validation, Spring Data, and REST.
- **Frontend:** Next.js with the App Router, React, TypeScript, and Tailwind CSS.
- **Frontend/backend boundary:** Spring Boot owns the domain model, business rules, persistence, authentication/authorization decisions, and public REST API. Use Next.js Route Handlers only when a thin backend-for-frontend layer is genuinely useful for session handling, proxying, or UI-specific response composition. Do not duplicate core business logic in Next.js.
- **NestJS:** Not part of the planned stack. Do not add a parallel NestJS backend beside Spring Boot. Introduce NestJS only after an ADR proves a distinct independently deployable TypeScript service is necessary, or if the project explicitly replaces Spring Boot—which would conflict with the current Java/Oracle portfolio objective.
- **Database:** Oracle Database for relational data first; add Oracle AI Vector Search in Phase 2. Select and document one supported database version instead of mixing version labels.
- **Database migrations:** Flyway or Liquibase; select one and use it consistently.
- **API documentation:** OpenAPI/Swagger.
- **AI/ML:** Python service only when Phase 2 or Phase 3 requires it; do not replace the Java core backend.
- **Local environment:** Docker Compose.
- **Testing:** JUnit 5, focused unit tests, integration tests, and Testcontainers where practical.
- **CI:** GitHub Actions for backend tests/build and frontend lint/test/build.
- **Observability later:** OpenTelemetry, Prometheus, Grafana, plus explicit log and trace storage.
- **Cloud later:** OCI and Terraform after the local product works.

Do not silently introduce Kubernetes, Kafka, multiple databases, a service mesh, many OCI services, or a multi-agent framework. Propose and justify major dependencies before adding them.

## MVP Scope

The first vertical slice must allow a user to:

1. register a monitored software service;
2. create an incident for that service;
3. assign severity and status;
4. add timeline events or evidence;
5. move the incident through `OPEN -> INVESTIGATING -> RESOLVED`;
6. view the incident list and details in the dashboard;
7. preserve resolution notes for future retrieval.

Suggested initial domain objects:

- `User`
- `Team`
- `MonitoredService`
- `Incident`
- `IncidentEvent`
- `Evidence`
- `Runbook`
- `Resolution`

Suggested incident fields include identifier, title, description, severity, status, affected service, detection time, assignee, timestamps, evidence, resolution summary, and audit metadata.

## Explicit Non-Goals for the MVP

- Six independent Spring Boot microservices
- A parallel NestJS backend duplicating the Spring Boot API
- Spring Cloud Gateway
- Kubernetes
- Terraform or OCI deployment
- Advanced machine learning
- A complicated multi-agent system
- Automatic production remediation
- Live ingestion from many external monitoring providers
- Fabricated AI confidence or unsupported root-cause claims

Use simulated telemetry or well-documented sample incidents until the core workflow is reliable.

## Repository Layout Direction

Prefer the following simple monorepo structure unless the existing code requires something different:

```text
backend/             Spring Boot modular backend
frontend/            Next.js, React, and TypeScript dashboard
ai-service/          Python AI/ML service, introduced later
infra/               Docker, observability, OCI, and Terraform assets
docs/
  adr/               Architecture Decision Records
  architecture/      Architecture and data-flow documentation
  api/               API examples or generated documentation notes
```

Do not create empty placeholder directories solely to make the repository appear larger.

## Engineering Rules for Codex

1. Read this file, the README, existing ADRs, and relevant source files before editing.
2. Preserve user changes and avoid unrelated rewrites.
3. Implement the smallest end-to-end slice that produces observable value.
4. Prefer simple, explicit code over premature abstractions.
5. Keep domain logic outside controllers and UI components.
6. Validate all external input and return consistent API errors.
7. Never commit credentials, tokens, private keys, local secrets, generated build output, IDE metadata, or environment-specific configuration.
8. Use environment variables and an `.env.example` containing names only, never real secrets.
9. Add or update tests for meaningful behavior changes.
10. Run the relevant build, test, lint, and formatting commands before declaring work complete.
11. If a command cannot run, report the exact blocker; never claim unexecuted tests passed.
12. Keep README statements aligned with implemented functionality. Clearly label planned features.
13. Update an ADR when changing service boundaries, persistence strategy, authentication, messaging, AI architecture, or deployment strategy.
14. Ask before adding a major production dependency or changing the agreed MVP scope.
15. Use Conventional Commit style for proposed commit messages, such as `feat(incidents): add incident creation endpoint`.

## API and Domain Conventions

- Prefer resource-oriented routes under `/api/v1`.
- Use DTOs at API boundaries; do not expose persistence entities directly.
- Keep timestamps in UTC and use ISO 8601 representations externally.
- Use explicit enums for incident severity and status.
- Preserve incident history through events/audit records rather than silently overwriting important operational facts.
- Define pagination for collection endpoints before datasets grow.
- Generate database schema changes through versioned migrations.
- Keep analysis requests auditable: record the model/provider, prompt or workflow version, retrieved evidence references, result, and timestamps without storing secrets.

## AI Quality and Safety Rules

- Retrieved evidence must remain distinguishable from generated explanation.
- Recommendations must cite their incident, runbook, log, or metric evidence when available.
- Treat AI output as decision support.
- Require human confirmation before an operational action.
- Do not grant an AI component unrestricted database, shell, cloud, or production access.
- Use least-privilege, allow-listed tools for the future investigation agent.
- Provide a deterministic fallback when the AI service is unavailable; core incident management must continue working.

## Work Procedure

For each implementation task:

1. Inspect current repository state and active instructions.
2. Restate the requested outcome and identify the affected components.
3. Propose a short plan for non-trivial work.
4. Implement a focused change without unrelated refactoring.
5. Run relevant verification.
6. Review the diff for secrets, generated files, misleading documentation, and accidental scope expansion.
7. Summarize changed files, verification results, limitations, and one sensible next step.

## Definition of Done

A task is complete only when:

- the requested behavior is implemented;
- the change matches the current project phase;
- relevant tests pass;
- build/lint checks pass where applicable;
- no secrets or unwanted generated files are included;
- documentation is accurate;
- the final response states what was verified and what remains unverified.

## Immediate Next Milestone

The next implementation milestone after documentation is a minimal Spring Boot bootstrap that includes:

- a reproducible build;
- `/actuator/health` or an equivalent health endpoint;
- one smoke test;
- a project-appropriate `.gitignore`;
- a GitHub Actions workflow that builds and tests the backend.

Do not begin AI, RAG, microservices, or OCI implementation before the core incident vertical slice and its tests are working.

## Code Review Rules

- Flag claims in documentation that describe unimplemented features as completed.
- Flag AI recommendations that lack evidence, uncertainty, or human validation.
- Flag secrets, insecure defaults, broad permissions, and sensitive values in logs.
- Flag new infrastructure or service boundaries that are not justified by the current project phase or an ADR.
- Flag changes that bypass tests or replace focused implementation with large unrelated refactors.
