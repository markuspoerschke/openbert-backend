CREATE TABLE oparl_raw_agenda_item
(
    id              UUID         NOT NULL,
    number          VARCHAR(255),
    "order"         INTEGER,
    name            VARCHAR(255),
    public          BOOLEAN,
    result          VARCHAR(255),
    resolution_text VARCHAR(255),
    start           TIMESTAMP WITHOUT TIME ZONE,
    "end"           TIMESTAMP WITHOUT TIME ZONE,
    license         VARCHAR(255),
    web             VARCHAR(255),
    created         BYTEA        NOT NULL,
    modified        BYTEA        NOT NULL,
    url             VARCHAR(255) NOT NULL,
    deleted         BOOLEAN      NOT NULL,
    system_id       UUID
);

CREATE TABLE oparl_raw_body
(
    id                  UUID         NOT NULL,
    short_name          VARCHAR(255),
    name                VARCHAR(255) NOT NULL,
    website             VARCHAR(255),
    license             VARCHAR(255),
    license_valid_since TIMESTAMP WITHOUT TIME ZONE,
    oparl_since         TIMESTAMP WITHOUT TIME ZONE,
    ags                 VARCHAR(255),
    rgs                 VARCHAR(255),
    contact_email       VARCHAR(255),
    contact_name        VARCHAR(255),
    classification      VARCHAR(255),
    web                 VARCHAR(255),
    created             BYTEA        NOT NULL,
    modified            BYTEA        NOT NULL,
    url                 VARCHAR(255) NOT NULL,
    deleted             BOOLEAN      NOT NULL,
    system_id           UUID
);

CREATE TABLE oparl_raw_consultation
(
    id            UUID         NOT NULL,
    authoritative BOOLEAN,
    role          VARCHAR(255),
    license       VARCHAR(255),
    web           VARCHAR(255),
    created       BYTEA        NOT NULL,
    modified      BYTEA        NOT NULL,
    url           VARCHAR(255) NOT NULL,
    deleted       BOOLEAN      NOT NULL,
    system_id     UUID
);

CREATE TABLE oparl_raw_file
(
    id                   UUID         NOT NULL,
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
    web                  VARCHAR(255),
    created              BYTEA        NOT NULL,
    modified             BYTEA        NOT NULL,
    url                  VARCHAR(255) NOT NULL,
    deleted              BOOLEAN      NOT NULL,
    system_id            UUID
);

CREATE TABLE oparl_raw_legislative_term
(
    id         UUID         NOT NULL,
    name       VARCHAR(255),
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    license    VARCHAR(255),
    web        VARCHAR(255),
    created    BYTEA        NOT NULL,
    modified   BYTEA        NOT NULL,
    url        VARCHAR(255) NOT NULL,
    deleted    BOOLEAN      NOT NULL,
    system_id  UUID
);

CREATE TABLE oparl_raw_location
(
    id             UUID         NOT NULL,
    description    VARCHAR(255),
    street_address VARCHAR(255),
    postal_code    VARCHAR(255),
    locality       VARCHAR(255),
    sub_locality   VARCHAR(255),
    room           VARCHAR(255),
    created        BYTEA        NOT NULL,
    modified       BYTEA        NOT NULL,
    url            VARCHAR(255) NOT NULL,
    deleted        BOOLEAN      NOT NULL,
    system_id      UUID
);

CREATE TABLE oparl_raw_meeting
(
    id            UUID         NOT NULL,
    name          VARCHAR(255),
    meeting_state VARCHAR(255),
    cancelled     BOOLEAN,
    start         TIMESTAMP WITHOUT TIME ZONE,
    "end"         TIMESTAMP WITHOUT TIME ZONE,
    license       VARCHAR(255),
    web           VARCHAR(255),
    created       BYTEA        NOT NULL,
    modified      BYTEA        NOT NULL,
    url           VARCHAR(255) NOT NULL,
    deleted       BOOLEAN      NOT NULL,
    system_id     UUID
);

CREATE TABLE oparl_raw_membership
(
    id           UUID         NOT NULL,
    role         VARCHAR(255),
    voting_right BOOLEAN,
    start_date   TIMESTAMP WITHOUT TIME ZONE,
    end_date     TIMESTAMP WITHOUT TIME ZONE,
    license      VARCHAR(255),
    web          VARCHAR(255),
    created      BYTEA        NOT NULL,
    modified     BYTEA        NOT NULL,
    url          VARCHAR(255) NOT NULL,
    deleted      BOOLEAN      NOT NULL,
    system_id    UUID
);

CREATE TABLE oparl_raw_organization
(
    id                UUID         NOT NULL,
    name              VARCHAR(255),
    short_name        VARCHAR(255),
    organization_type VARCHAR(255),
    classification    VARCHAR(255),
    start_date        TIMESTAMP WITHOUT TIME ZONE,
    end_date          TIMESTAMP WITHOUT TIME ZONE,
    website           VARCHAR(255),
    license           VARCHAR(255),
    web               VARCHAR(255),
    created           BYTEA        NOT NULL,
    modified          BYTEA        NOT NULL,
    url               VARCHAR(255) NOT NULL,
    deleted           BOOLEAN      NOT NULL,
    system_id         UUID
);

CREATE TABLE oparl_raw_paper
(
    id         UUID         NOT NULL,
    name       VARCHAR(255),
    reference  VARCHAR(255),
    date       TIMESTAMP WITHOUT TIME ZONE,
    paper_type VARCHAR(255),
    license    VARCHAR(255),
    web        VARCHAR(255),
    created    BYTEA        NOT NULL,
    modified   BYTEA        NOT NULL,
    url        VARCHAR(255) NOT NULL,
    deleted    BOOLEAN      NOT NULL,
    system_id  UUID
);

CREATE TABLE oparl_raw_person
(
    id              UUID         NOT NULL,
    name            VARCHAR(255),
    family_name     VARCHAR(255),
    given_name      VARCHAR(255),
    form_of_address VARCHAR(255),
    affix           VARCHAR(255),
    gender          VARCHAR(255),
    life            VARCHAR(255),
    life_source     VARCHAR(255),
    license         VARCHAR(255),
    web             VARCHAR(255),
    created         BYTEA        NOT NULL,
    modified        BYTEA        NOT NULL,
    url             VARCHAR(255) NOT NULL,
    deleted         BOOLEAN      NOT NULL,
    system_id       UUID
);

CREATE TABLE oparl_raw_system
(
    id            UUID         NOT NULL,
    oparl_version VARCHAR(255) NOT NULL,
    license       VARCHAR(255),
    name          VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255),
    contact_name  VARCHAR(255),
    website       VARCHAR(255),
    vendor        VARCHAR(255),
    product       VARCHAR(255),
    web           VARCHAR(255),
    created       BYTEA        NOT NULL,
    modified      BYTEA        NOT NULL,
    url           VARCHAR(255) NOT NULL,
    deleted       BOOLEAN      NOT NULL,
    system_id     UUID
);

CREATE TABLE oparl_resolved_agenda_item
(
    id              UUID         NOT NULL,
    number          VARCHAR(255),
    "order"         INTEGER,
    name            VARCHAR(255),
    public          BOOLEAN,
    result          VARCHAR(255),
    resolution_text VARCHAR(255),
    start           TIMESTAMP WITHOUT TIME ZONE,
    "end"           TIMESTAMP WITHOUT TIME ZONE,
    license         VARCHAR(255),
    web             VARCHAR(255),
    created         BYTEA        NOT NULL,
    modified        BYTEA        NOT NULL,
    url             VARCHAR(255) NOT NULL,
    deleted         BOOLEAN      NOT NULL,
    system_id       UUID
);

CREATE TABLE oparl_resolved_body
(
    id                  UUID         NOT NULL,
    short_name          VARCHAR(255),
    name                VARCHAR(255) NOT NULL,
    website             VARCHAR(255),
    license             VARCHAR(255),
    license_valid_since TIMESTAMP WITHOUT TIME ZONE,
    oparl_since         TIMESTAMP WITHOUT TIME ZONE,
    ags                 VARCHAR(255),
    rgs                 VARCHAR(255),
    contact_email       VARCHAR(255),
    contact_name        VARCHAR(255),
    classification      VARCHAR(255),
    web                 VARCHAR(255),
    created             BYTEA        NOT NULL,
    modified            BYTEA        NOT NULL,
    url                 VARCHAR(255) NOT NULL,
    deleted             BOOLEAN      NOT NULL,
    system_id           UUID
);

CREATE TABLE oparl_resolved_consultation
(
    id            UUID         NOT NULL,
    authoritative BOOLEAN,
    role          VARCHAR(255),
    license       VARCHAR(255),
    web           VARCHAR(255),
    created       BYTEA        NOT NULL,
    modified      BYTEA        NOT NULL,
    url           VARCHAR(255) NOT NULL,
    deleted       BOOLEAN      NOT NULL,
    system_id     UUID
);

CREATE TABLE oparl_resolved_file
(
    id                   UUID         NOT NULL,
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
    web                  VARCHAR(255),
    created              BYTEA        NOT NULL,
    modified             BYTEA        NOT NULL,
    url                  VARCHAR(255) NOT NULL,
    deleted              BOOLEAN      NOT NULL,
    system_id            UUID
);

CREATE TABLE oparl_resolved_legislative_term
(
    id         UUID         NOT NULL,
    name       VARCHAR(255),
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    license    VARCHAR(255),
    web        VARCHAR(255),
    created    BYTEA        NOT NULL,
    modified   BYTEA        NOT NULL,
    url        VARCHAR(255) NOT NULL,
    deleted    BOOLEAN      NOT NULL,
    system_id  UUID
);

CREATE TABLE oparl_resolved_location
(
    id             UUID         NOT NULL,
    description    VARCHAR(255),
    street_address VARCHAR(255),
    postal_code    VARCHAR(255),
    locality       VARCHAR(255),
    sub_locality   VARCHAR(255),
    room           VARCHAR(255),
    created        BYTEA        NOT NULL,
    modified       BYTEA        NOT NULL,
    url            VARCHAR(255) NOT NULL,
    deleted        BOOLEAN      NOT NULL,
    system_id      UUID
);

CREATE TABLE oparl_resolved_meeting
(
    id            UUID         NOT NULL,
    name          VARCHAR(255),
    meeting_state VARCHAR(255),
    cancelled     BOOLEAN,
    start         TIMESTAMP WITHOUT TIME ZONE,
    "end"         TIMESTAMP WITHOUT TIME ZONE,
    license       VARCHAR(255),
    web           VARCHAR(255),
    created       BYTEA        NOT NULL,
    modified      BYTEA        NOT NULL,
    url           VARCHAR(255) NOT NULL,
    deleted       BOOLEAN      NOT NULL,
    system_id     UUID
);

CREATE TABLE oparl_resolved_membership
(
    id           UUID         NOT NULL,
    role         VARCHAR(255),
    voting_right BOOLEAN,
    start_date   TIMESTAMP WITHOUT TIME ZONE,
    end_date     TIMESTAMP WITHOUT TIME ZONE,
    license      VARCHAR(255),
    web          VARCHAR(255),
    created      BYTEA        NOT NULL,
    modified     BYTEA        NOT NULL,
    url          VARCHAR(255) NOT NULL,
    deleted      BOOLEAN      NOT NULL,
    system_id    UUID
);

CREATE TABLE oparl_resolved_organization
(
    id                UUID         NOT NULL,
    name              VARCHAR(255),
    short_name        VARCHAR(255),
    organization_type VARCHAR(255),
    classification    VARCHAR(255),
    start_date        TIMESTAMP WITHOUT TIME ZONE,
    end_date          TIMESTAMP WITHOUT TIME ZONE,
    website           VARCHAR(255),
    license           VARCHAR(255),
    web               VARCHAR(255),
    created           BYTEA        NOT NULL,
    modified          BYTEA        NOT NULL,
    url               VARCHAR(255) NOT NULL,
    deleted           BOOLEAN      NOT NULL,
    system_id         UUID
);

CREATE TABLE oparl_resolved_paper
(
    id         UUID         NOT NULL,
    name       VARCHAR(255),
    reference  VARCHAR(255),
    date       TIMESTAMP WITHOUT TIME ZONE,
    paper_type VARCHAR(255),
    license    VARCHAR(255),
    web        VARCHAR(255),
    created    BYTEA        NOT NULL,
    modified   BYTEA        NOT NULL,
    url        VARCHAR(255) NOT NULL,
    deleted    BOOLEAN      NOT NULL,
    system_id  UUID
);

CREATE TABLE oparl_resolved_person
(
    id              UUID         NOT NULL,
    name            VARCHAR(255),
    family_name     VARCHAR(255),
    given_name      VARCHAR(255),
    form_of_address VARCHAR(255),
    affix           VARCHAR(255),
    gender          VARCHAR(255),
    life            VARCHAR(255),
    life_source     VARCHAR(255),
    license         VARCHAR(255),
    web             VARCHAR(255),
    created         BYTEA        NOT NULL,
    modified        BYTEA        NOT NULL,
    url             VARCHAR(255) NOT NULL,
    deleted         BOOLEAN      NOT NULL,
    system_id       UUID
);

CREATE TABLE oparl_resolved_system
(
    id            UUID         NOT NULL,
    oparl_version VARCHAR(255) NOT NULL,
    license       VARCHAR(255),
    name          VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255),
    contact_name  VARCHAR(255),
    website       VARCHAR(255),
    vendor        VARCHAR(255),
    product       VARCHAR(255),
    web           VARCHAR(255),
    created       BYTEA        NOT NULL,
    modified      BYTEA        NOT NULL,
    url           VARCHAR(255) NOT NULL,
    deleted       BOOLEAN      NOT NULL,
    system_id     UUID
);

ALTER TABLE oparl_raw_agenda_item ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_raw_body ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_raw_consultation ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_raw_file ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_raw_legislative_term ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_raw_location ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_raw_meeting ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_raw_membership ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_raw_organization ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_raw_paper ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_raw_person ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_raw_system ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_resolved_agenda_item ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_resolved_body ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_resolved_consultation ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_resolved_file ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_resolved_legislative_term ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_resolved_location ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_resolved_meeting ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_resolved_membership ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_resolved_organization ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_resolved_paper ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_resolved_person ALTER COLUMN system_id SET NOT NULL;
ALTER TABLE oparl_resolved_system ALTER COLUMN system_id SET NOT NULL;

ALTER TABLE oparl_raw_agenda_item
    ADD CONSTRAINT pk_oparl_raw_agendaitem PRIMARY KEY (id);

ALTER TABLE oparl_raw_body
    ADD CONSTRAINT pk_oparl_raw_body PRIMARY KEY (id);

ALTER TABLE oparl_raw_consultation
    ADD CONSTRAINT pk_oparl_raw_consultation PRIMARY KEY (id);

ALTER TABLE oparl_raw_file
    ADD CONSTRAINT pk_oparl_raw_file PRIMARY KEY (id);

ALTER TABLE oparl_raw_legislative_term
    ADD CONSTRAINT pk_oparl_raw_legislativeterm PRIMARY KEY (id);

ALTER TABLE oparl_raw_location
    ADD CONSTRAINT pk_oparl_raw_location PRIMARY KEY (id);

ALTER TABLE oparl_raw_meeting
    ADD CONSTRAINT pk_oparl_raw_meeting PRIMARY KEY (id);

ALTER TABLE oparl_raw_membership
    ADD CONSTRAINT pk_oparl_raw_membership PRIMARY KEY (id);

ALTER TABLE oparl_raw_organization
    ADD CONSTRAINT pk_oparl_raw_organization PRIMARY KEY (id);

ALTER TABLE oparl_raw_paper
    ADD CONSTRAINT pk_oparl_raw_paper PRIMARY KEY (id);

ALTER TABLE oparl_raw_person
    ADD CONSTRAINT pk_oparl_raw_person PRIMARY KEY (id);

ALTER TABLE oparl_raw_system
    ADD CONSTRAINT pk_oparl_raw_system PRIMARY KEY (id);

ALTER TABLE oparl_resolved_agenda_item
    ADD CONSTRAINT pk_oparl_resolved_agendaitem PRIMARY KEY (id);

ALTER TABLE oparl_resolved_body
    ADD CONSTRAINT pk_oparl_resolved_body PRIMARY KEY (id);

ALTER TABLE oparl_resolved_consultation
    ADD CONSTRAINT pk_oparl_resolved_consultation PRIMARY KEY (id);

ALTER TABLE oparl_resolved_file
    ADD CONSTRAINT pk_oparl_resolved_file PRIMARY KEY (id);

ALTER TABLE oparl_resolved_legislative_term
    ADD CONSTRAINT pk_oparl_resolved_legislativeterm PRIMARY KEY (id);

ALTER TABLE oparl_resolved_location
    ADD CONSTRAINT pk_oparl_resolved_location PRIMARY KEY (id);

ALTER TABLE oparl_resolved_meeting
    ADD CONSTRAINT pk_oparl_resolved_meeting PRIMARY KEY (id);

ALTER TABLE oparl_resolved_membership
    ADD CONSTRAINT pk_oparl_resolved_membership PRIMARY KEY (id);

ALTER TABLE oparl_resolved_organization
    ADD CONSTRAINT pk_oparl_resolved_organization PRIMARY KEY (id);

ALTER TABLE oparl_resolved_paper
    ADD CONSTRAINT pk_oparl_resolved_paper PRIMARY KEY (id);

ALTER TABLE oparl_resolved_person
    ADD CONSTRAINT pk_oparl_resolved_person PRIMARY KEY (id);

ALTER TABLE oparl_resolved_system
    ADD CONSTRAINT pk_oparl_resolved_system PRIMARY KEY (id);

ALTER TABLE oparl_raw_agenda_item
    ADD CONSTRAINT uc_oparl_raw_agendaitem_url UNIQUE (system_id, url);

ALTER TABLE oparl_raw_body
    ADD CONSTRAINT uc_oparl_raw_body_url UNIQUE (system_id, url);

ALTER TABLE oparl_raw_consultation
    ADD CONSTRAINT uc_oparl_raw_consultation_url UNIQUE (system_id, url);

ALTER TABLE oparl_raw_file
    ADD CONSTRAINT uc_oparl_raw_file_url UNIQUE (system_id, url);

ALTER TABLE oparl_raw_legislative_term
    ADD CONSTRAINT uc_oparl_raw_legislativeterm_url UNIQUE (system_id, url);

ALTER TABLE oparl_raw_location
    ADD CONSTRAINT uc_oparl_raw_location_url UNIQUE (system_id, url);

ALTER TABLE oparl_raw_meeting
    ADD CONSTRAINT uc_oparl_raw_meeting_url UNIQUE (system_id, url);

ALTER TABLE oparl_raw_membership
    ADD CONSTRAINT uc_oparl_raw_membership_url UNIQUE (system_id, url);

ALTER TABLE oparl_raw_organization
    ADD CONSTRAINT uc_oparl_raw_organization_url UNIQUE (system_id, url);

ALTER TABLE oparl_raw_paper
    ADD CONSTRAINT uc_oparl_raw_paper_url UNIQUE (system_id, url);

ALTER TABLE oparl_raw_person
    ADD CONSTRAINT uc_oparl_raw_person_url UNIQUE (system_id, url);

ALTER TABLE oparl_raw_system
    ADD CONSTRAINT uc_oparl_raw_system_url UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_agenda_item
    ADD CONSTRAINT uc_oparl_resolved_agendaitem_url UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_body
    ADD CONSTRAINT uc_oparl_resolved_body_url UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_consultation
    ADD CONSTRAINT uc_oparl_resolved_consultation_url UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_file
    ADD CONSTRAINT uc_oparl_resolved_file_url UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_legislative_term
    ADD CONSTRAINT uc_oparl_resolved_legislativeterm_url UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_location
    ADD CONSTRAINT uc_oparl_resolved_location_url UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_meeting
    ADD CONSTRAINT uc_oparl_resolved_meeting_url UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_membership
    ADD CONSTRAINT uc_oparl_resolved_membership_url UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_organization
    ADD CONSTRAINT uc_oparl_resolved_organization_url UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_paper
    ADD CONSTRAINT uc_oparl_resolved_paper_url UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_person
    ADD CONSTRAINT uc_oparl_resolved_person_url UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_system
    ADD CONSTRAINT uc_oparl_resolved_system_url UNIQUE (system_id, url);