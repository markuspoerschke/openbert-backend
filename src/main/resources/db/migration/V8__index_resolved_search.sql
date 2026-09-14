-- Preserve literal, case-insensitive substring search without assembling the corpus on reads.
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Text-only inputs have a stable representation, unlike concat_ws(any) or array_to_string(anyarray).
-- This immutable helper allows both scalar fields and text arrays in stored generated columns.
CREATE FUNCTION oparl_search_text(VARIADIC fields text[]) RETURNS text
    LANGUAGE sql IMMUTABLE PARALLEL SAFE
    AS $$ SELECT array_to_string(fields, ' ') $$;

ALTER TABLE oparl_resolved_body
    ADD COLUMN searchable_text text GENERATED ALWAYS AS (
        lower(oparl_search_text(
            short_name, name, website, license, ags, rgs, oparl_search_text(VARIADIC equivalent),
            contact_email, contact_name, classification, oparl_search_text(VARIADIC keyword), web, url
        ))
    ) STORED;
CREATE INDEX oparl_resolved_body_search_trgm_idx
    ON oparl_resolved_body USING gin (searchable_text gin_trgm_ops)
    WHERE NOT deleted;

ALTER TABLE oparl_resolved_legislative_term
    ADD COLUMN searchable_text text GENERATED ALWAYS AS (
        lower(oparl_search_text(
            name, license, oparl_search_text(VARIADIC keyword), web, url
        ))
    ) STORED;
CREATE INDEX oparl_resolved_legislative_term_search_trgm_idx
    ON oparl_resolved_legislative_term USING gin (searchable_text gin_trgm_ops)
    WHERE NOT deleted;

ALTER TABLE oparl_resolved_organization
    ADD COLUMN searchable_text text GENERATED ALWAYS AS (
        lower(oparl_search_text(
            name, short_name, oparl_search_text(VARIADIC post), organization_type, classification, website,
            license, oparl_search_text(VARIADIC keyword), web, url
        ))
    ) STORED;
CREATE INDEX oparl_resolved_organization_search_trgm_idx
    ON oparl_resolved_organization USING gin (searchable_text gin_trgm_ops)
    WHERE NOT deleted;

ALTER TABLE oparl_resolved_person
    ADD COLUMN searchable_text text GENERATED ALWAYS AS (
        lower(oparl_search_text(
            name, family_name, given_name, form_of_address, affix, oparl_search_text(VARIADIC title),
            gender, oparl_search_text(VARIADIC phone), oparl_search_text(VARIADIC email),
            oparl_search_text(VARIADIC status), life, life_source, license,
            oparl_search_text(VARIADIC keyword), web, url
        ))
    ) STORED;
CREATE INDEX oparl_resolved_person_search_trgm_idx
    ON oparl_resolved_person USING gin (searchable_text gin_trgm_ops)
    WHERE NOT deleted;

ALTER TABLE oparl_resolved_membership
    ADD COLUMN searchable_text text GENERATED ALWAYS AS (
        lower(oparl_search_text(
            role, license, oparl_search_text(VARIADIC keyword), web, url
        ))
    ) STORED;
CREATE INDEX oparl_resolved_membership_search_trgm_idx
    ON oparl_resolved_membership USING gin (searchable_text gin_trgm_ops)
    WHERE NOT deleted;

ALTER TABLE oparl_resolved_meeting
    ADD COLUMN searchable_text text GENERATED ALWAYS AS (
        lower(oparl_search_text(
            name, meeting_state, license, oparl_search_text(VARIADIC keyword), web, url
        ))
    ) STORED;
CREATE INDEX oparl_resolved_meeting_search_trgm_idx
    ON oparl_resolved_meeting USING gin (searchable_text gin_trgm_ops)
    WHERE NOT deleted;

ALTER TABLE oparl_resolved_agenda_item
    ADD COLUMN searchable_text text GENERATED ALWAYS AS (
        lower(oparl_search_text(
            number, name, result, resolution_text, license, oparl_search_text(VARIADIC keyword), web, url
        ))
    ) STORED;
CREATE INDEX oparl_resolved_agenda_item_search_trgm_idx
    ON oparl_resolved_agenda_item USING gin (searchable_text gin_trgm_ops)
    WHERE NOT deleted;

ALTER TABLE oparl_resolved_paper
    ADD COLUMN searchable_text text GENERATED ALWAYS AS (
        lower(oparl_search_text(
            name, reference, paper_type, license, oparl_search_text(VARIADIC keyword), web, url
        ))
    ) STORED;
CREATE INDEX oparl_resolved_paper_search_trgm_idx
    ON oparl_resolved_paper USING gin (searchable_text gin_trgm_ops)
    WHERE NOT deleted;

ALTER TABLE oparl_resolved_consultation
    ADD COLUMN searchable_text text GENERATED ALWAYS AS (
        lower(oparl_search_text(
            role, license, oparl_search_text(VARIADIC keyword), web, url
        ))
    ) STORED;
CREATE INDEX oparl_resolved_consultation_search_trgm_idx
    ON oparl_resolved_consultation USING gin (searchable_text gin_trgm_ops)
    WHERE NOT deleted;

ALTER TABLE oparl_resolved_file
    ADD COLUMN searchable_text text GENERATED ALWAYS AS (
        lower(oparl_search_text(
            name, file_name, mime_type, text, access_url, download_url, external_service_url, file_license,
            license, oparl_search_text(VARIADIC keyword), web, url
        ))
    ) STORED;
CREATE INDEX oparl_resolved_file_search_trgm_idx
    ON oparl_resolved_file USING gin (searchable_text gin_trgm_ops)
    WHERE NOT deleted;

ALTER TABLE oparl_resolved_location
    ADD COLUMN searchable_text text GENERATED ALWAYS AS (
        lower(oparl_search_text(
            description, geojson::text, street_address, postal_code, locality, sub_locality, room, license,
            oparl_search_text(VARIADIC keyword), web, url
        ))
    ) STORED;
CREATE INDEX oparl_resolved_location_search_trgm_idx
    ON oparl_resolved_location USING gin (searchable_text gin_trgm_ops)
    WHERE NOT deleted;
