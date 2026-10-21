CREATE TABLE enrich_target
(
    id               UUID        NOT NULL,
    raw_entity_id    UUID        NOT NULL,
    entity_type      VARCHAR(32) NOT NULL,
    created_at       TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    last_enriched_at TIMESTAMP(6) WITHOUT TIME ZONE,
    modified_at      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    reference_count  INTEGER     NOT NULL,
    scheduled_at     TIMESTAMP(6) WITHOUT TIME ZONE,
    job_id           UUID,
    CONSTRAINT pk_enrich_target PRIMARY KEY (id),
    CONSTRAINT uq_enrich_raw_entity_type UNIQUE (raw_entity_id, entity_type)
);

CREATE INDEX ix_enrich_target_due
    ON enrich_target (reference_count DESC, created_at)
    WHERE reference_count > 0 AND job_id IS NULL;

CREATE TABLE oparl_enriched_location
(
    id                      UUID         NOT NULL,
    override_street_address BOOLEAN      NOT NULL,
    street_address          VARCHAR(255),
    override_postal_code    BOOLEAN      NOT NULL,
    postal_code             VARCHAR(255),
    override_room           BOOLEAN      NOT NULL,
    room                    VARCHAR(255),
    override_description    BOOLEAN      NOT NULL,
    description             TEXT,
    override_locality       BOOLEAN      NOT NULL,
    locality                VARCHAR(255),
    created                 TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified                TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_oparl_enriched_location PRIMARY KEY (id),
    CONSTRAINT fk_oparl_enriched_location_raw FOREIGN KEY (id) REFERENCES oparl_raw_location (id)
);
