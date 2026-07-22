CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE oparl_raw_agenda_item
(
    id              UUID                        NOT NULL,
    number          VARCHAR(255),
    "order"         INTEGER,
    name            VARCHAR(255),
    public          BOOLEAN,
    result          VARCHAR(255),
    resolution_text VARCHAR(255),
    start           TIMESTAMP WITHOUT TIME ZONE,
    "end"           TIMESTAMP WITHOUT TIME ZONE,
    license         VARCHAR(255),
    keyword         TEXT[]                      NOT NULL,
    web             VARCHAR(255),
    system_id       UUID                        NOT NULL,
    url             VARCHAR(1024)               NOT NULL,
    created         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted         BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_raw_agendaitem PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_body
(
    id                  UUID                        NOT NULL,
    short_name          VARCHAR(255),
    name                VARCHAR(255)                NOT NULL,
    website             VARCHAR(255),
    license             VARCHAR(255),
    license_valid_since TIMESTAMP WITHOUT TIME ZONE,
    oparl_since         TIMESTAMP WITHOUT TIME ZONE,
    ags                 VARCHAR(255),
    rgs                 VARCHAR(255),
    equivalent          TEXT[]                      NOT NULL,
    contact_email       VARCHAR(255),
    contact_name        VARCHAR(255),
    classification      VARCHAR(255),
    keyword             TEXT[]                      NOT NULL,
    web                 VARCHAR(255),
    system_id           UUID                        NOT NULL,
    url                 VARCHAR(1024)               NOT NULL,
    created             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted             BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_raw_body PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_consultation
(
    id            UUID                        NOT NULL,
    authoritative BOOLEAN,
    role          VARCHAR(255),
    license       VARCHAR(255),
    keyword       TEXT[]                      NOT NULL,
    web           VARCHAR(255),
    system_id     UUID                        NOT NULL,
    url           VARCHAR(1024)               NOT NULL,
    created       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted       BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_raw_consultation PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_file
(
    id                   UUID                        NOT NULL,
    name                 VARCHAR(255),
    file_name            VARCHAR(255),
    mime_type            VARCHAR(255),
    date                 TIMESTAMP WITHOUT TIME ZONE,
    size                 INTEGER,
    sha1checksum         VARCHAR(255),
    sha512checksum       VARCHAR(255),
    text                 VARCHAR(255),
    access_url           VARCHAR(255),
    download_url         VARCHAR(255),
    external_service_url VARCHAR(255),
    file_license         VARCHAR(255),
    license              VARCHAR(255),
    keyword              TEXT[]                      NOT NULL,
    web                  VARCHAR(255),
    system_id            UUID                        NOT NULL,
    url                  VARCHAR(1024)               NOT NULL,
    created              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted              BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_raw_file PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_legislative_term
(
    id         UUID                        NOT NULL,
    name       VARCHAR(255),
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    license    VARCHAR(255),
    keyword    TEXT[]                      NOT NULL,
    web        VARCHAR(255),
    system_id  UUID                        NOT NULL,
    url        VARCHAR(1024)               NOT NULL,
    created    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted    BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_raw_legislativeterm PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_location
(
    id             UUID                        NOT NULL,
    description    VARCHAR(255),
    street_address VARCHAR(255),
    postal_code    VARCHAR(255),
    locality       VARCHAR(255),
    sub_locality   VARCHAR(255),
    room           VARCHAR(255),
    keyword        TEXT[]                      NOT NULL,
    system_id      UUID                        NOT NULL,
    url            VARCHAR(1024)               NOT NULL,
    created        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted        BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_raw_location PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_meeting
(
    id            UUID                        NOT NULL,
    name          VARCHAR(255),
    meeting_state VARCHAR(255),
    cancelled     BOOLEAN,
    start         TIMESTAMP WITHOUT TIME ZONE,
    "end"         TIMESTAMP WITHOUT TIME ZONE,
    license       VARCHAR(255),
    keyword       TEXT[]                      NOT NULL,
    web           VARCHAR(255),
    system_id     UUID                        NOT NULL,
    url           VARCHAR(1024)               NOT NULL,
    created       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted       BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_raw_meeting PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_membership
(
    id           UUID                        NOT NULL,
    role         VARCHAR(255),
    voting_right BOOLEAN,
    start_date   TIMESTAMP WITHOUT TIME ZONE,
    end_date     TIMESTAMP WITHOUT TIME ZONE,
    license      VARCHAR(255),
    keyword      TEXT[]                      NOT NULL,
    web          VARCHAR(255),
    system_id    UUID                        NOT NULL,
    url          VARCHAR(1024)               NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted      BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_raw_membership PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_organization
(
    id                UUID                        NOT NULL,
    name              VARCHAR(255),
    short_name        VARCHAR(255),
    post              TEXT[]                      NOT NULL,
    organization_type VARCHAR(255),
    classification    VARCHAR(255),
    start_date        TIMESTAMP WITHOUT TIME ZONE,
    end_date          TIMESTAMP WITHOUT TIME ZONE,
    website           VARCHAR(255),
    license           VARCHAR(255),
    keyword           TEXT[]                      NOT NULL,
    web               VARCHAR(255),
    system_id         UUID                        NOT NULL,
    url               VARCHAR(1024)               NOT NULL,
    created           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted           BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_raw_organization PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_paper
(
    id         UUID                        NOT NULL,
    name       VARCHAR(255),
    reference  VARCHAR(255),
    date       TIMESTAMP WITHOUT TIME ZONE,
    paper_type VARCHAR(255),
    license    VARCHAR(255),
    keyword    TEXT[]                      NOT NULL,
    web        VARCHAR(255),
    system_id  UUID                        NOT NULL,
    url        VARCHAR(1024)               NOT NULL,
    created    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted    BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_raw_paper PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_person
(
    id              UUID                        NOT NULL,
    name            VARCHAR(255),
    family_name     VARCHAR(255),
    given_name      VARCHAR(255),
    form_of_address VARCHAR(255),
    affix           VARCHAR(255),
    title           TEXT[]                      NOT NULL,
    gender          VARCHAR(255),
    phone           TEXT[]                      NOT NULL,
    email           TEXT[]                      NOT NULL,
    status          TEXT[]                      NOT NULL,
    life            VARCHAR(255),
    life_source     VARCHAR(255),
    license         VARCHAR(255),
    keyword         TEXT[]                      NOT NULL,
    web             VARCHAR(255),
    system_id       UUID                        NOT NULL,
    url             VARCHAR(1024)               NOT NULL,
    created         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted         BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_raw_person PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_system
(
    id                   UUID                        NOT NULL,
    oparl_version        VARCHAR(255)                NOT NULL,
    other_oparl_versions TEXT[]                      NOT NULL,
    license              VARCHAR(255),
    name                 VARCHAR(255)                NOT NULL,
    contact_email        VARCHAR(255),
    contact_name         VARCHAR(255),
    website              VARCHAR(255),
    vendor               VARCHAR(255),
    product              VARCHAR(255),
    web                  VARCHAR(255),
    system_id            UUID                        NOT NULL,
    url                  VARCHAR(1024)               NOT NULL,
    created              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted              BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_raw_system PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_agenda_item
(
    id              UUID                        NOT NULL,
    number          VARCHAR(255),
    "order"         INTEGER,
    name            VARCHAR(255),
    public          BOOLEAN,
    result          VARCHAR(255),
    resolution_text VARCHAR(255),
    start           TIMESTAMP WITHOUT TIME ZONE,
    "end"           TIMESTAMP WITHOUT TIME ZONE,
    license         VARCHAR(255),
    keyword         TEXT[]                      NOT NULL,
    web             VARCHAR(255),
    system_id       UUID                        NOT NULL,
    url             VARCHAR(1024)               NOT NULL,
    created         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted         BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_resolved_agendaitem PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_body
(
    id                  UUID                        NOT NULL,
    short_name          VARCHAR(255),
    name                VARCHAR(255)                NOT NULL,
    website             VARCHAR(255),
    license             VARCHAR(255),
    license_valid_since TIMESTAMP WITHOUT TIME ZONE,
    oparl_since         TIMESTAMP WITHOUT TIME ZONE,
    ags                 VARCHAR(255),
    rgs                 VARCHAR(255),
    equivalent          TEXT[]                      NOT NULL,
    contact_email       VARCHAR(255),
    contact_name        VARCHAR(255),
    classification      VARCHAR(255),
    keyword             TEXT[]                      NOT NULL,
    web                 VARCHAR(255),
    system_id           UUID                        NOT NULL,
    url                 VARCHAR(1024)               NOT NULL,
    created             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted             BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_resolved_body PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_consultation
(
    id            UUID                        NOT NULL,
    authoritative BOOLEAN,
    role          VARCHAR(255),
    license       VARCHAR(255),
    keyword       TEXT[]                      NOT NULL,
    web           VARCHAR(255),
    system_id     UUID                        NOT NULL,
    url           VARCHAR(1024)               NOT NULL,
    created       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted       BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_resolved_consultation PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_file
(
    id                   UUID                        NOT NULL,
    name                 VARCHAR(255),
    file_name            VARCHAR(255),
    mime_type            VARCHAR(255),
    date                 TIMESTAMP WITHOUT TIME ZONE,
    size                 INTEGER,
    sha1checksum         VARCHAR(255),
    sha512checksum       VARCHAR(255),
    text                 VARCHAR(255),
    access_url           VARCHAR(255),
    download_url         VARCHAR(255),
    external_service_url VARCHAR(255),
    file_license         VARCHAR(255),
    license              VARCHAR(255),
    keyword              TEXT[]                      NOT NULL,
    web                  VARCHAR(255),
    system_id            UUID                        NOT NULL,
    url                  VARCHAR(1024)               NOT NULL,
    created              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted              BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_resolved_file PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_legislative_term
(
    id         UUID                        NOT NULL,
    name       VARCHAR(255),
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    license    VARCHAR(255),
    keyword    TEXT[]                      NOT NULL,
    web        VARCHAR(255),
    system_id  UUID                        NOT NULL,
    url        VARCHAR(1024)               NOT NULL,
    created    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted    BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_resolved_legislativeterm PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_location
(
    id             UUID                        NOT NULL,
    description    VARCHAR(255),
    street_address VARCHAR(255),
    postal_code    VARCHAR(255),
    locality       VARCHAR(255),
    sub_locality   VARCHAR(255),
    room           VARCHAR(255),
    keyword        TEXT[]                      NOT NULL,
    system_id      UUID                        NOT NULL,
    url            VARCHAR(1024)               NOT NULL,
    created        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted        BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_resolved_location PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_meeting
(
    id            UUID                        NOT NULL,
    name          VARCHAR(255),
    meeting_state VARCHAR(255),
    cancelled     BOOLEAN,
    start         TIMESTAMP WITHOUT TIME ZONE,
    "end"         TIMESTAMP WITHOUT TIME ZONE,
    license       VARCHAR(255),
    keyword       TEXT[]                      NOT NULL,
    web           VARCHAR(255),
    system_id     UUID                        NOT NULL,
    url           VARCHAR(1024)               NOT NULL,
    created       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted       BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_resolved_meeting PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_membership
(
    id           UUID                        NOT NULL,
    role         VARCHAR(255),
    voting_right BOOLEAN,
    start_date   TIMESTAMP WITHOUT TIME ZONE,
    end_date     TIMESTAMP WITHOUT TIME ZONE,
    license      VARCHAR(255),
    keyword      TEXT[]                      NOT NULL,
    web          VARCHAR(255),
    system_id    UUID                        NOT NULL,
    url          VARCHAR(1024)               NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted      BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_resolved_membership PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_organization
(
    id                UUID                        NOT NULL,
    name              VARCHAR(255),
    short_name        VARCHAR(255),
    post              TEXT[]                      NOT NULL,
    organization_type VARCHAR(255),
    classification    VARCHAR(255),
    start_date        TIMESTAMP WITHOUT TIME ZONE,
    end_date          TIMESTAMP WITHOUT TIME ZONE,
    website           VARCHAR(255),
    license           VARCHAR(255),
    keyword           TEXT[]                      NOT NULL,
    web               VARCHAR(255),
    system_id         UUID                        NOT NULL,
    url               VARCHAR(1024)               NOT NULL,
    created           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted           BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_resolved_organization PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_paper
(
    id         UUID                        NOT NULL,
    name       VARCHAR(255),
    reference  VARCHAR(255),
    date       TIMESTAMP WITHOUT TIME ZONE,
    paper_type VARCHAR(255),
    license    VARCHAR(255),
    keyword    TEXT[]                      NOT NULL,
    web        VARCHAR(255),
    system_id  UUID                        NOT NULL,
    url        VARCHAR(1024)               NOT NULL,
    created    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted    BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_resolved_paper PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_person
(
    id              UUID                        NOT NULL,
    name            VARCHAR(255),
    family_name     VARCHAR(255),
    given_name      VARCHAR(255),
    form_of_address VARCHAR(255),
    affix           VARCHAR(255),
    title           TEXT[]                      NOT NULL,
    gender          VARCHAR(255),
    phone           TEXT[]                      NOT NULL,
    email           TEXT[]                      NOT NULL,
    status          TEXT[]                      NOT NULL,
    life            VARCHAR(255),
    life_source     VARCHAR(255),
    license         VARCHAR(255),
    keyword         TEXT[]                      NOT NULL,
    web             VARCHAR(255),
    system_id       UUID                        NOT NULL,
    url             VARCHAR(1024)               NOT NULL,
    created         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted         BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_resolved_person PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_system
(
    id                   UUID                        NOT NULL,
    oparl_version        VARCHAR(255)                NOT NULL,
    other_oparl_versions TEXT[]                      NOT NULL,
    license              VARCHAR(255),
    name                 VARCHAR(255)                NOT NULL,
    contact_email        VARCHAR(255),
    contact_name         VARCHAR(255),
    website              VARCHAR(255),
    vendor               VARCHAR(255),
    product              VARCHAR(255),
    web                  VARCHAR(255),
    system_id            UUID                        NOT NULL,
    url                  VARCHAR(1024)               NOT NULL,
    created              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted              BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oparl_resolved_system PRIMARY KEY (id)
);

CREATE TABLE oparl_system
(
    id  UUID          NOT NULL,
    url VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_system PRIMARY KEY (id)
);

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255)
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);