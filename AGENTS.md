# AGENTS.md

## Goal of this project

- Scrape OParl server and save data in raw tables
- Enrich data with AI and other external sources and store it in resolved
- Provide data through OParl API again

## Important Commands

- Build application: `./gradlew build`
- Test application: `./gradlew test`
- Format code: `./gradlew ktlintFormat`

## Principles

- Follow OParl data structure for raw and resolved entities
- Allow raw entities to be less strict (like missing relationships etc.)
- Always provide test data in [V9999__test-data.sql](src/test/resources/db/migration/V9999__test-data.sql)

## Architecture

This project follows **Domain-Driven Design (DDD)** and **Hexagonal Architecture (Ports and Adapters)** patterns.

### Domain-Driven Design (DDD)

- Organize code around **domain models** and **business logic** rather than technical concerns
- Use **ubiquitous language** from the problem domain (OParl specification)
- Separate **domain layer** (core business logic) from **application layer** (use cases) and **infrastructure layer** (technical details)
- Define clear **bounded contexts** for different subdomains (e.g., OParl entities, enrichment, API)
- Use **entities**, **value objects**, **aggregates**, **repositories**, and **domain services** to model the domain

### Hexagonal Architecture (Ports and Adapters)

- **Core application** (domain + application) is isolated from external concerns
- **Ports** define interfaces for interaction with the outside world:
  - **Driving ports** (inbound): API endpoints, CLI commands that trigger use cases
  - **Driven ports** (outbound): Repository interfaces, external service clients
- **Adapters** implement ports to connect to real infrastructure:
  - **Driving adapters**: REST controllers, gRPC servers, CLI handlers
  - **Driven adapters**: Database implementations, HTTP clients, message brokers
- Dependencies flow **inward**: Application depends on abstractions (ports), not on concrete implementations (adapters)

This ensures the core business logic remains **testable**, **maintainable**, and **independent** of frameworks and external systems.

## Further Resources

- See [OParl-1.1.md](docs/oparl/OParl-1.1.md) for the specs of Oparl
- Follow wording defined in [GLOSSARY.md](docs/GLOSSARY.md)
