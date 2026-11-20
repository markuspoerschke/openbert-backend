# AGENTS.md

## Goal of this project

- Scrape OParl servers and save the data in raw tables.
- Enrich and resolve the data into resolved tables.
- Provide the data again through an OParl API and MCP tools.

## Important commands

- Build application: `./gradlew build`
- Test application: `./gradlew test`
- Format code: `./gradlew ktlintFormat`

See [CONTRIBUTING.md](CONTRIBUTING.md) for running the app locally, configuration, and deployment.

## Principles

- Follow the OParl data structure for raw and resolved entities.
- Allow raw entities to be less strict (like missing relationships etc.).
- Always provide test data in [V9999__test-data.sql](src/test/resources/db/migration/V9999__test-data.sql).
- Never edit an applied Flyway migration — add a new one.

## Architecture

The project follows domain-driven design and hexagonal architecture. Each bounded context (currently `parldata`) has three layers under `src/main/kotlin/nrw/openbert/backend/<context>`:

- `domain` — the model and business logic; depends on nothing.
- `application` — use cases such as crawl, enrich, resolve, search; depends on domain only.
- `adapter` — HTTP, MCP, crawler, and persistence integrations; depends inward.

Ports (inbound and outbound) are interfaces. The rules are enforced by `ArchitectureTest` — run `./gradlew test --tests 'nrw.openbert.backend.architecture.ArchitectureTest'`.

## Further resources

- [OParl-1.1.md](docs/oparl/OParl-1.1.md) — the OParl specification
- [GLOSSARY.md](docs/GLOSSARY.md) — follow the wording defined there
- [docs/crawling.md](docs/crawling.md) — crawler rules for list representations and embedded objects
