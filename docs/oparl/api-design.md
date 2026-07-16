# OParl API Design Overview

## Goals
- Expose the data stored in the canonical OParl Doctrine entities under `src/Entity` via an HTTP API that complies with [OParl 1.1](./OParl-1.1.md).
- Enrich every response with the additional insights stored in enrichment entities (e.g., `App\\Entity\\EnrichedPaper`, `App\\Entity\\EnrichedLocation`) whenever they exist.
- Keep the API stateless, cache-friendly, and fully discoverable through hyperlinks in line with the OParl specification.

## High-Level Architecture
1. **Routing layer**: All endpoints live under `/oparl/v1.1` (yielding public URLs like `https://oparl.example.org/oparl/v1.1/system`). Symfony attribute routes in `App\\Controller\\Oparl` map HTTP requests to dedicated controllers per aggregate (`SystemController`, `BodyController`, `PaperController`, ...).
2. **Controller layer**: Controllers remain thin and delegate data loading to read services. They ensure HTTP semantics (status codes, pagination parameters, content negotiation limited to JSON) and return DTO instances.
3. **Read services**: Each aggregate has a read service (e.g., `SystemReader`, `BodyReader`) that orchestrates Doctrine repositories, eager loads related objects, and merges enrichment entities.
4. **Mapping layer**: Mapper classes convert Doctrine entities into the generated DTOs under `generated/Oparl`. They are also responsible for injecting enrichment attributes and flagging their origin (canonical vs. enriched).
5. **Serialization**: Symfony Serializer converts DTOs to JSON. A custom normalizer/encoder adds `_links`, pagination metadata, and ensures property naming matches the OParl schema.
6. **Response helpers**: Shared helpers encapsulate pagination link building, error payloads, and HTTP caching headers.

## Data & Enrichment Flow
- Canonical data flows from Doctrine repositories for `App\\Entity\\Oparl*` entities.
- Enrichment data flows from the enrichment repositories (`App\\Entity\\EnrichedPaper`, `App\\Entity\\EnrichedLocation`, `App\\Entity\\EnrichedLocationMention`).
- Each mapper receives canonical entities plus optional enrichment records and produces a composite DTO.
- When enrichment is missing, the mapper omits the fields or marks them as `null` according to DTO expectations; consumers can rely on an `enriched` metadata block for provenance.
- Newly added enrichment entities can be integrated by extending the relevant mapper and updating the reader service without changing controllers.

## Versioning & Discoverability
- The base URL `/oparl/v1.1` reflects the protocol version and combines with the deployment host (e.g., `https://oparl.example.org/oparl/v1.1`). Future revisions can coexist at `/oparl/v1.2` without breaking existing clients.
- The `System` endpoint advertises the main collections (`/bodies`, `/people`, `/papers`, ...). Each collection endpoint returns paginated lists with `links.next`, `links.prev`, etc., matching the JSON examples in the OParl spec.
- Individual resources expose hyperlink arrays (e.g., `meeting`, `agendaItem`, `paper`) referencing the corresponding endpoints.

## Pagination & Filtering
- Collection endpoints accept the spec-defined query parameters (`offset`, `limit`) along with resource-specific filters (`classification`, `keyword`, `fullText`).
- Controllers validate query params and normalize them through a shared request validator.
- Pagination metadata and navigation links are added via the response helper to keep controller code minimal.

## Error Handling & Caching
- Canonical `deleted` flags map to HTTP 410 responses with OParl-compliant error payloads.
- Missing resources return HTTP 404.
- Invalid parameters raise HTTP 400 with a structured error list.
- Controllers and helpers attach `Last-Modified`/`ETag` headers derived from entity timestamps so clients can leverage conditional requests.

## Testing Strategy
- Unit tests cover mappers to ensure canonical and enriched data combine as expected.
- Integration tests hit each endpoint via Symfony's HTTP kernel, verifying pagination, filtering, hypermedia links, and error payloads.
- JSON schema validation checks responses against the generated DTO schemas (where feasible) to maintain spec compliance.

## Deployment Considerations
- The API is read-only and can be scaled horizontally; cache headers enable CDN caching.
- Monitoring hooks (e.g., per-endpoint logs, latency metrics) ensure visibility of client usage.
- TODO items in the project root track per-endpoint progress and must be updated as functionality ships.
