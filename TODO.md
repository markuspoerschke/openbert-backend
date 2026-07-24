# TODO

## OParl endpoint implementation order

The tasks below are ordered from smaller changes with fewer dependencies to
larger, relationship-heavy changes. Paths are relative to `/oparl/v1.1`.

### 1. Align the existing Organization and Person endpoints

- [x] Move the single Organization endpoint from `/organization/{organization}`
  to `/body/{body}/organization/{organization}`.
- [x] Move the single Person endpoint from `/person/{person}` to
  `/body/{body}/person/{person}`.
- [x] Update `OparlUrlGenerator` and the controller tests to use the body-scoped
  URLs.
- [x] Look up each resource by both Body ID and resource ID so a resource cannot
  be retrieved through another Body.

### 2. LegislativeTerm

Depends only on Body and already has a DTO.

- [x] Add the Body-scoped list endpoint:
  `/body/{body}/legislativeterm`.
- [x] Add the single-resource endpoint:
  `/body/{body}/legislativeterm/{legislative-term}`.
- [x] Populate the embedded `Body.legislativeTerm` list from the same resolved
  data exposed by `Body.legislativeTermList`.

### 3. Location

The DTO already exists. Other resources can reuse Location once this endpoint is
available.

- [ ] Add the Body-scoped list endpoint: `/body/{body}/location`.
- [ ] Add the single-resource endpoint:
  `/body/{body}/location/{location}`.

### 4. File

File is reused by Paper, AgendaItem, and Meeting. Its `accessUrl` is mandatory in
OParl 1.1.

- [ ] Add the Body-scoped list endpoint: `/body/{body}/file`.
- [ ] Add the single-resource endpoint: `/body/{body}/file/{file}`.
- [ ] Ensure `accessUrl` serves the file without forcing an attachment download.

### 5. Membership

Depends on the existing Person and Organization endpoints and on
LegislativeTerm.

- [ ] Add the Body-scoped list endpoint: `/body/{body}/membership`.
- [ ] Add the single-resource endpoint:
  `/body/{body}/membership/{membership}`.
- [ ] Populate the embedded `Person.membership` list.
- [ ] Populate `Organization.membership` with Membership URLs.

### 6. Paper

Depends on Body, Person, Organization, Location, and File. Consultation can be
added to its embedded list when the Consultation endpoint is implemented.

- [ ] Add the Body-scoped list endpoint: `/body/{body}/paper`.
- [ ] Add the single-resource endpoint: `/body/{body}/paper/{paper}`.

### 7. Meeting

Meeting has the largest embedded object graph: Organization, Person, Location,
File, and AgendaItem. Implement its core fields and available relationships
first so AgendaItem can link to an existing Meeting endpoint.

- [ ] Add the Body-scoped list endpoint: `/body/{body}/meeting`.
- [ ] Add the single-resource endpoint: `/body/{body}/meeting/{meeting}`.

### 8. AgendaItem

Depends on Meeting and File. Its Consultation reference is completed in the next
step.

- [ ] Add the Body-scoped list endpoint: `/body/{body}/agendaitem`.
- [ ] Add the single-resource endpoint:
  `/body/{body}/agendaitem/{agenda-item}`.
- [ ] Ensure the mandatory `order` field is returned.

### 9. Consultation and meeting-graph integration

Consultation is the junction between Paper, AgendaItem, Meeting, and
Organization, so it is implemented after all four resource endpoints exist.

- [ ] Add the Body-scoped list endpoint: `/body/{body}/consultation`.
- [ ] Add the single-resource endpoint:
  `/body/{body}/consultation/{consultation}`.
- [ ] Populate the embedded `Paper.consultation` list.
- [ ] Link AgendaItem to Consultation.
- [ ] Populate the embedded `Meeting.agendaItem` list in its stable agenda order.

### 10. Organization-linked external lists

These endpoints are small filters, but depend on the Meeting and Consultation
relationships being available first.

- [ ] Add the Organization Meeting list:
  `/body/{body}/organization/{organization}/meeting`.
- [ ] Add the Organization Consultation list:
  `/body/{body}/organization/{organization}/consultation`.
- [ ] Expose both external-list URLs in the Organization response.

## Definition of done for every endpoint

- Add the resolved entity-to-Body relationship and database foreign key where it
  does not exist yet.
- Return only resolved resources belonging to the requested Body.
- Generate public URLs through `OparlUrlGenerator` instead of hard-coded values.
- Return HTTP 404 for unknown Body and resource IDs and for mismatched
  Body/resource combinations.
- Add representative data to `V9999__test-data.sql`.
- Add controller tests for list, single-resource, unknown Body, unknown resource,
  and mismatched Body/resource responses.
