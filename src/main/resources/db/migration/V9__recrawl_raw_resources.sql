-- Earlier processors marked list and inline objects as crawled without fetching
-- their resource URLs. Their provenance cannot be recovered, so refresh every
-- existing raw resource once. Preserve active jobs and list pagination targets.
WITH raw_resources AS (
    SELECT system_id, url, 'AGENDA_ITEM' AS entity_type FROM oparl_raw_agenda_item
    UNION ALL SELECT system_id, url, 'BODY' FROM oparl_raw_body
    UNION ALL SELECT system_id, url, 'CONSULTATION' FROM oparl_raw_consultation
    UNION ALL SELECT system_id, url, 'FILE' FROM oparl_raw_file
    UNION ALL SELECT system_id, url, 'LEGISLATIVE_TERM' FROM oparl_raw_legislative_term
    UNION ALL SELECT system_id, url, 'LOCATION' FROM oparl_raw_location
    UNION ALL SELECT system_id, url, 'MEETING' FROM oparl_raw_meeting
    UNION ALL SELECT system_id, url, 'MEMBERSHIP' FROM oparl_raw_membership
    UNION ALL SELECT system_id, url, 'ORGANIZATION' FROM oparl_raw_organization
    UNION ALL SELECT system_id, url, 'PAPER' FROM oparl_raw_paper
    UNION ALL SELECT system_id, url, 'PERSON' FROM oparl_raw_person
    UNION ALL SELECT system_id, url, 'SYSTEM' FROM oparl_raw_system
)
INSERT INTO crawl_target (id, system_id, url, entity_type, created_at, modified_at, reference_count)
SELECT gen_random_uuid(), system_id, url, entity_type, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1
FROM raw_resources
ON CONFLICT (system_id, url) DO UPDATE
SET entity_type = COALESCE(crawl_target.entity_type, EXCLUDED.entity_type),
    last_crawled_at = NULL,
    modified_at = EXCLUDED.modified_at,
    reference_count = GREATEST(crawl_target.reference_count, 1);
