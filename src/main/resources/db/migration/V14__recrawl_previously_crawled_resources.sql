-- Previous list processing could mark incomplete entity representations as crawled.
-- Revisit all completed resources, including list pages, using the corrected flow.
UPDATE crawl_target
SET last_crawled_at = NULL,
    reference_count = GREATEST(reference_count, 1),
    scheduled_at = NULL,
    job_id = NULL,
    modified_at = CURRENT_TIMESTAMP
WHERE last_crawled_at IS NOT NULL;
