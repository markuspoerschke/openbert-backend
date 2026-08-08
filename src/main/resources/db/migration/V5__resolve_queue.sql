CREATE TABLE resolve_target
(
    id               UUID          NOT NULL,
    raw_entity_id    UUID          NOT NULL,
    entity_type      VARCHAR(32)   NOT NULL,
    created_at       TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    last_resolved_at TIMESTAMP(6) WITHOUT TIME ZONE,
    modified_at      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    reference_count  INTEGER       NOT NULL,
    scheduled_at     TIMESTAMP(6) WITHOUT TIME ZONE,
    job_id            UUID,
    CONSTRAINT pk_resolve_target PRIMARY KEY (id),
    CONSTRAINT uq_resolve_raw_entity_type UNIQUE (raw_entity_id, entity_type)
);

CREATE INDEX ix_resolve_target_due
    ON resolve_target (reference_count DESC, created_at)
    WHERE reference_count > 0 AND job_id IS NULL;
