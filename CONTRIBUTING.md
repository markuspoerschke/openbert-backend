# Contributing

Thanks for your interest in Openbert. This document describes how to run, test, build, and deploy the project as a human contributor. AI coding agents should follow [AGENTS.md](AGENTS.md) instead.

## Run locally

Requires **JDK 25** and **Docker**. From the repository root:

```sh
docker compose up -d
./gradlew bootRun
```

After startup completes and Flyway has created the schema, run this once in another terminal to seed the Krefeld crawl:

```sh
docker compose exec -T postgres psql -U myuser -d mydatabase < docs/fixtures.sql
```

`compose.yaml` provides a PostgreSQL service named `postgres` with database `mydatabase` and user `myuser`. Data availability depends on the upstream server and background processing.

- **MCP:** `http://localhost:8080/mcp` (Streamable HTTP); tools: `oparl_body_list`, `oparl_search`, `oparl_get_resource`.
  Use `oparl_search` or `oparl_body_list` first, then pass the `id` of a result to `oparl_get_resource` to fetch that single resource.
- **API:** [OParl entry point](http://localhost:8080/oparl/v1.1/system).
- **API documentation:** [Swagger UI](http://localhost:8080/docs) to browse and try the HTTP endpoints; OpenAPI schema at `http://localhost:8080/docs/openapi.json`.
- **Tests and packaging:** `./gradlew build` (Docker required for Testcontainers).

## Configuration

Application-specific properties use the `app.` prefix and can be overridden through environment variables using Spring's relaxed binding.

| Property | Environment variable | Default | Description |
| --- | --- | --- | --- |
| `app.crawl.scheduler.enabled` | `APP_CRAWL_SCHEDULER_ENABLED` | `true` | Enables or disables the crawl cronjob. When set to `false`, no crawl targets are processed. |
| `app.crawl.scheduler.cron` | `APP_CRAWL_SCHEDULER_CRON` | `0 * * * * *` | Cron expression for the crawl cronjob (every 60 seconds by default). The special value `-` disables the schedule. |
| `app.enrich.scheduler.enabled` | `APP_ENRICH_SCHEDULER_ENABLED` | `true` | Enables or disables the enrichment cronjob. When set to `false`, no enrich targets are processed. |
| `app.enrich.scheduler.cron` | `APP_ENRICH_SCHEDULER_CRON` | `*/15 * * * * *` | Cron expression for the enrichment cronjob (every 15 seconds by default). The special value `-` disables the schedule. |
| `app.resolve.scheduler.enabled` | `APP_RESOLVE_SCHEDULER_ENABLED` | `true` | Enables or disables the resolve cronjob. When set to `false`, no resolve targets are processed. |
| `app.resolve.scheduler.cron` | `APP_RESOLVE_SCHEDULER_CRON` | `*/15 * * * * *` | Cron expression for the resolve cronjob (every 15 seconds by default). The special value `-` disables the schedule. |
| `app.crypto.key-base64` | `APP_CRYPTO_KEY_BASE64` | none (required) | Base64-encoded 256-bit AES key used for encrypting sensitive database fields (such as OParl system authorization headers). The app fails to start without it. |

### Encryption key

The application encrypts sensitive database fields (like OParl system authorization headers) using AES-256 GCM. Generate a secure Base64-encoded 256-bit key using OpenSSL:

```sh
openssl rand -base64 32
```

Provide this key in development or production via the `APP_CRYPTO_KEY_BASE64` environment variable.

All three queues (crawl, enrich, resolve) run directly inside their cronjobs: due targets are processed inline and paced by the cron expression. A failing target is marked with `failed_at`/`failure_reason` on its queue row and reported to Sentry; it is not retried automatically.

## Build, test, and format

- `./gradlew build` — full build: compiles, runs ktlint, Detekt, ArchUnit architecture tests, and all tests (Docker required for Testcontainers)
- `./gradlew test` — tests only
- `./gradlew ktlintFormat` — format Kotlin sources

CI runs `./gradlew build --no-daemon` on every pull request and every push to `main` (see [.github/workflows/test.yml](.github/workflows/test.yml)). Open pull requests against `main` and keep changes small and focused.

## Static analysis

Run `./gradlew detekt` to analyze production and test Kotlin sources. Detekt also runs through `check` when executing `./gradlew build`, including the CI build. Reports are written to `build/reports/detekt/` in HTML, XML, Markdown and SARIF formats.

Detekt findings fail the build. The project uses Detekt 2.0.0-alpha.6 for compatibility with JDK 25 and Gradle 9.

## Architecture tests

ArchUnit checks production bytecode to enforce dependencies between the domain, application, and adapter layers in each bounded context. Dependencies point inward: domain code cannot depend on application code or adapters, and application code cannot depend on adapters. The root package contains application bootstrap code, which no other layer may depend on. Every production class must belong to one of these layers.

Inbound and outbound ports must be interfaces. Port contracts and their models may depend on domain types and other port contracts, but not on application implementations. Existing JPA annotations, Spring components, and Spring Data repository ports remain supported; the rules constrain dependencies within the backend. Dependencies within the adapter layer are allowed, including crawler access to the OParl client and MCP access to API mappers.

Run `./gradlew test --tests 'nrw.openbert.backend.architecture.ArchitectureTest'` to check the architecture without starting Spring or Docker. These tests also run with `./gradlew test` and the CI `./gradlew build`; violations fail the build. URL persistence uses an automatically applied JPA converter so domain entities do not reference the persistence adapter.

## Docker image

The standalone backend image is built entirely inside Docker, so the build machine does not need JDK or Gradle installed:

```sh
docker build -t openbert-backend .
```

Run the image against a PostgreSQL database by supplying the connection settings at runtime:

```sh
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://database-host:5432/openbert \
  -e SPRING_DATASOURCE_USERNAME=openbert \
  -e SPRING_DATASOURCE_PASSWORD=change-me \
  -e APP_CRYPTO_KEY_BASE64="<base64-key>" \
  openbert-backend
```

Do not put database credentials in the image, Dockerfile, or repository. The existing `compose.yaml` continues to provide PostgreSQL only for local development; PostgreSQL is not bundled into the backend image.

### Coolify

Configure Coolify to build the repository's root `Dockerfile`, then set:

- Container port: `8080`
- `SPRING_DATASOURCE_URL`: the external PostgreSQL JDBC URL, for example `jdbc:postgresql://postgres-host:5432/openbert`
- `SPRING_DATASOURCE_USERNAME`: the PostgreSQL user
- `SPRING_DATASOURCE_PASSWORD`: the PostgreSQL password, stored as a secret
- `APP_CRYPTO_KEY_BASE64`: the Base64-encoded AES key, stored as a secret
