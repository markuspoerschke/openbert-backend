# AGENTS.md

## Goal of this project

- Scrape OParl server and save data in raw tables
- Enrich data with AI and other external sources and store it in resolved
- Provide data through OParl API again

## Important Commands

- Build application: `./gradlew build`
- Test application: `./gradlew test`

## Principles

- Follow OParl data structure for raw and resolved entities
- Allow raw entities to be less strict (like missing relationships etc.)
- Always provide test data in [V9999__test-data.sql](src/test/resources/db/migration/V9999__test-data.sql)
- Only modify [V1__init.sql](src/main/resources/db/migration/V1__init.sql) for now, since this application is not deployed yet.

## Further Resources

- See [OParl-1.1.md](docs/oparl/OParl-1.1.md) for the specs of Oparl
