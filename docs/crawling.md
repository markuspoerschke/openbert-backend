# Crawling list representations

Objects from external list pages (`OparlListDto.data`) and objects embedded
in other entities can omit fields. They may initialize a raw entity, but must
never update an existing raw entity, including its inferred parent references.
Only a response fetched from the object's own URL may update it and mark that
URL as crawled. Every entity created from a list is queued for that fetch.
Pagination continues independently, and processing a page marks only the page
as crawled.

The nested objects processed by the crawler are:

| Parent | Embedded properties |
| --- | --- |
| Body | `legislativeTerm`, `location` |
| Person | `membership`, `locationObject` |
| Organization | `location` |
| Meeting | `agendaItem`, `location`, `invitation`, `resultsProtocol`, `verbatimProtocol`, `auxiliaryFile` |
| Paper | `consultation`, `location`, `mainFile`, `auxiliaryFile` |
| AgendaItem | `resolutionFile`, `auxiliaryFile` |

Persons are created from external list pages, such as the list referenced by
`Body.person`. Meeting participants and paper originators are URL references;
they queue individual person URLs without creating a raw person immediately.

Some entity types were discoverable only through embedded objects in OParl 1.0.
This does not prevent an individual fetch: the [OParl specification](oparl/OParl-1.1.md#objekttypen)
explicitly requires every object to be retrievable at its ID, including embedded
objects. Embedded objects may omit their parent reference, so initialization
preserves that reference from the parent before saving the new entity.

Existing rows marked as crawled by the previous list-processing behavior cannot
be distinguished from rows fetched individually. Migration
`V14__recrawl_previously_crawled_resources.sql` requeues every previously crawled
resource, including external list pages, so the corrected flow can fetch complete
data. It clears the completed crawl and scheduling markers and ensures a positive
reference count. Pending resources keep their current queue state.
