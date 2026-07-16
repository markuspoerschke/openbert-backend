# Mandatory OParl 1.1 exposure

This note captures the "must" level requirements from the specification so we know which resources, fields, and navigation behaviors our API has to provide.

## Resources

The OParl 1.1 schema requires us to publish the following resource types: `System`, `Body`, `LegislativeTerm`, `Organization`, `Person`, `Membership`, `Meeting`, `AgendaItem`, `Paper`, `Consultation`, `File`, and `Location`.

### System
- Must expose a separate `System` object per supported OParl version, each advertising the spec URL via `oparlVersion` (`https://schema.oparl.org/1.1/`).
- Has to publish the entry point to the body collection through the `body` external list URL.

### Body
- Provides the canonical long-form `name`.
- Must offer external list URLs for every primary collection owned by the body: `organization`, `person`, `meeting`, `paper`, `agendaItem`, `consultation`, `file`, `locationList`, `legislativeTermList`, and `membership`.
- Must embed the current `legislativeTerm` collection.

### AgendaItem
- Requires a numeric `order` that reflects the position of the agenda item within its parent meeting.

### File
- Must expose an `accessUrl` that delivers the general download entry point for the binary.

## Navigation rules

### Object lists
- Attributes either embed objects directly or point to dedicated list endpoints; the specification defines the approach for each attribute, and embedded responses must not include deleted objects.

### External lists
- Each paged list response has to return the `data`, `pagination`, and `links` sections. The `pagination` object reports totals/page metadata when available, and `links.next` is mandatory on every non-terminal page. Servers must maintain a stable ordering across requests.
- Servers must support the filters `created_since`, `created_until`, `modified_since`, and `modified_until` on every external list endpoint and preserve the applied filters inside pagination links.
- When `modified_since` is set, list responses have to include matching deleted objects so that clients can synchronize removals; otherwise deleted objects must be omitted.
- Exposing `limit` is optional for servers, but clients cannot rely on the server honoring it.
- The optional `omit_internal=true` hint should suppress embedded lists for the attributes enumerated by the specification (auxiliary files, agenda items, legislative terms, memberships, and paper locations).

### Files
- Every file has to respond to HTTP `GET`, and the `accessUrl` must not force an attachment download via `Content-Disposition`; a separate `downloadUrl` can exist for that behavior.
