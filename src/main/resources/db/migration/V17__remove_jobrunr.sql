-- Enrich and resolve targets record failures in the queue table like crawl targets already do.
ALTER TABLE enrich_target
    ADD COLUMN failed_at TIMESTAMP(6) WITHOUT TIME ZONE,
    ADD COLUMN failure_reason VARCHAR(1024);

ALTER TABLE resolve_target
    ADD COLUMN failed_at TIMESTAMP(6) WITHOUT TIME ZONE,
    ADD COLUMN failure_reason VARCHAR(1024);

-- The job_id columns only stored Jobrunr job ids; the partial due indexes reference them and are dropped with them.
ALTER TABLE crawl_target DROP COLUMN job_id;
ALTER TABLE enrich_target DROP COLUMN job_id;
ALTER TABLE resolve_target DROP COLUMN job_id;

CREATE INDEX ix_enrich_target_due
    ON enrich_target (reference_count DESC, created_at)
    WHERE reference_count > 0 AND failed_at IS NULL;

CREATE INDEX ix_resolve_target_due
    ON resolve_target (reference_count DESC, created_at)
    WHERE reference_count > 0 AND failed_at IS NULL;

-- Jobrunr managed its own tables in this datasource.
DO $$
DECLARE
    jobrunr_table TEXT;
BEGIN
    FOR jobrunr_table IN
        SELECT tablename
        FROM pg_tables
        WHERE schemaname = 'public' AND tablename LIKE 'jobrunr_%'
    LOOP
        EXECUTE format('DROP TABLE IF EXISTS %I CASCADE', jobrunr_table);
    END LOOP;
END $$;
