# TODO

## Remaining body-linked OParl endpoints

The following URL generators used by the Body endpoint still return `"TODO"`.
Each resource needs a body-scoped external list endpoint and a single-resource
endpoint, following the existing Organization endpoint pattern.

- [x] **Person**
  - Implement the external list referenced by `Body.person` (`personList`).
  - Implement the single Person GET endpoint.
- [x] **Meeting**
  - Implement the external list referenced by `Body.meeting` (`meetingList`).
  - Implement the single Meeting GET endpoint.
- [ ] **Paper**
  - Implement the external list referenced by `Body.paper` (`paperList`).
  - Implement the single Paper GET endpoint.
- [ ] **AgendaItem**
  - Implement the external list referenced by `Body.agendaItem` (`agendaItemList`).
  - Implement the single AgendaItem GET endpoint.
- [ ] **Consultation**
  - Implement the external list referenced by `Body.consultation` (`consultationList`).
  - Implement the single Consultation GET endpoint.
- [ ] **File**
  - Implement the external list referenced by `Body.file` (`fileList`).
  - Implement the single File GET endpoint.
- [ ] **LegislativeTerm**
  - Implement the external list referenced by `Body.legislativeTermList` (`legislativeTermList`).
  - Implement the single LegislativeTerm GET endpoint.
  - Populate the embedded `Body.legislativeTerm` list from the same resolved data.
- [ ] **Membership**
  - Implement the external list referenced by `Body.membership` (`membershipList`).
  - Implement the single Membership GET endpoint.
- [ ] **Location**
  - Implement the external list referenced by `Body.locationList` (`locationList`).
  - Implement the single Location GET endpoint.

For every item:

- Add the resolved entity-to-Body relationship and database foreign key where it
  does not exist yet.
- Return only resolved resources belonging to the requested Body.
- Generate public URLs through `OparlUrlGenerator` instead of hard-coded values.
- Return HTTP 404 for unknown Body and resource IDs.
- Add representative data to `V9999__test-data.sql` and controller tests for the
  list, single-resource, and not-found responses.
