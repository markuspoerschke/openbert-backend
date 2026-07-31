CREATE TABLE crawl_target
(
    id              UUID         NOT NULL,
    system_id       UUID         NOT NULL,
    url             VARCHAR(1024) NOT NULL,
    created_at      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    last_crawled_at TIMESTAMP(6) WITHOUT TIME ZONE,
    last_indexed_at TIMESTAMP(6) WITHOUT TIME ZONE,
    modified_at     TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    reference_count INTEGER      NOT NULL,
    scheduled_at    TIMESTAMP(6) WITHOUT TIME ZONE,
    job_id          UUID,
    CONSTRAINT pk_crawl_target PRIMARY KEY (id)
);

ALTER TABLE crawl_target
    ADD CONSTRAINT uq_oparl_system_url UNIQUE (system_id, url);

ALTER TABLE crawl_target
    ADD CONSTRAINT FK_CRAWL_TARGET_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);
