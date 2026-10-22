-- Apply the completed deterministic address rules to locations enriched before this release.
INSERT INTO enrich_target
    (id, raw_entity_id, entity_type, created_at, modified_at, reference_count)
SELECT gen_random_uuid(), id, 'LOCATION', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1
FROM oparl_raw_location
ON CONFLICT (raw_entity_id, entity_type) DO UPDATE
SET reference_count = enrich_target.reference_count + 1,
    modified_at = EXCLUDED.modified_at,
    scheduled_at = NULL,
    job_id = NULL;
