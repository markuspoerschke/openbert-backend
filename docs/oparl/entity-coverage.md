# Doctrine and enrichment coverage by OParl resource

This catalogue maps the required OParl 1.1 resources to their Doctrine entities in `src/Entity` and notes any enrichment data we already maintain. Use it alongside [`mandatory-resources.md`](mandatory-resources.md) when planning API exposure.

| OParl resource | Doctrine entity | Enrichment entity | Notes |
| -------------- | --------------- | ----------------- | ----- |
| System | `App\Entity\OparlSystem` | — | Canonical system metadata only. |
| Body | `App\Entity\OparlBody` | — | Canonical body registry. |
| LegislativeTerm | `App\Entity\OparlLegislativeTerm` | — | No enrichment captured yet. |
| Organization | `App\Entity\OparlOrganization` | — | Membership/person overlays still TBD. |
| Person | `App\Entity\OparlPerson` | — | No biography or AI overlays captured. |
| Membership | `App\Entity\OparlMembership` | — | Canonical membership graph only. |
| Meeting | `App\Entity\OparlMeeting` | — | Agenda enrichment pending. |
| AgendaItem | `App\Entity\OparlAgendaItem` | — | No enrichment at present. |
| Paper | `App\Entity\OparlPaper` | `App\Entity\EnrichedPaper`, `App\Entity\EnrichedLocationMention` | Subject/summary overlays and detected location mentions. |
| Consultation | `App\Entity\OparlConsultation` | — | Canonical consultation only. |
| File | `App\Entity\OparlFile` | `App\Entity\EnrichedFile` | Stores extracted content, subject, and summary. |
| Location | `App\Entity\OparlLocation` | `App\Entity\EnrichedLocation` | Stores normalized addresses and geocoding. |

## Gap highlights

- Non-document resources (system, body, organization, person, membership, meeting, agenda item, consultation) currently expose only canonical Doctrine entities. Future enrichment ideas include biographies, keyword tagging, and agenda analytics.
- Paper enrichment already supports text summaries plus location mentions; extending this to link enriched locations back into the public API will be necessary.
- File enrichment captures extracted content but we still need to define how/when to expose it to clients.
- Enriched locations exist independently of canonical locations. We should plan workflows that reconcile them and capture provenance when linking back to OParl objects.
