# Openbert – Glossary

Wording used across the project, the documentation, and the code. OParl object
type names are the English names from the [OParl specification](oparl/OParl-1.1.md).

## OParl terms

| German                 | Abbreviation (DE) | English                                     | Abbreviation (EN) |
|------------------------|-------------------|---------------------------------------------|-------------------|
| Ratsinformationssystem | RIS               | Council Information System                  | CIS               |
| Körperschaft           | –                 | Body                                        | –                 |
| Wahlperiode            | –                 | Legislative Term                            | –                 |
| Gremium                | –                 | Organization                                | –                 |
| Person                 | –                 | Person                                      | –                 |
| Mitgliedschaft         | –                 | Membership                                  | –                 |
| Sitzung                | –                 | Meeting                                     | –                 |
| Tagesordnungspunkt     | TOP               | Agenda Item                                 | –                 |
| Drucksache             | –                 | Paper                                       | –                 |
| Beratung               | –                 | Consultation                                | –                 |
| Datei                  | –                 | File                                        | –                 |
| Ort                    | –                 | Location                                    | –                 |
| –                      | OParl             | OParl (specification for open council data) | OParl             |

## Project terms

| Term           | German                      | Meaning                                                                                                                               |
|----------------|-----------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| Crawler        | Crawler                     | Fetches OParl servers and writes the responses into the raw tables.                                                                   |
| Raw / resolved | Rohdaten / aufgelöste Daten | Raw tables keep source data as-is, even incomplete; resolved tables apply enrichment and build the relationships used by the API.     |
| Enrichment     | Anreicherung                | Deterministic enrichment of raw data, e.g. location normalization with separately stored overrides.                                   |
| Resolution     | Auflösung                   | Applies overrides and builds resolved entities and their relationships.                                                               |
| MCP            | –                           | Model Context Protocol; the main entry point for clients, with the tools `oparl_body_list`, `oparl_search`, and `oparl_get_resource`. |
| OParl proxy    | –                           | Adapter that reads council information systems without an OParl API (e.g. HTML crawler).                                              |
