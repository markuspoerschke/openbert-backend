CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE oparl_raw_agenda_item
(
    id              UUID          NOT NULL,
    number          VARCHAR(255),
    "order"         INTEGER,
    name            VARCHAR(500),
    public          BOOLEAN,
    result          VARCHAR(255),
    resolution_text VARCHAR(255),
    start           TIMESTAMP(6) WITHOUT TIME ZONE,
    "end"           TIMESTAMP(6) WITHOUT TIME ZONE,
    license         VARCHAR(255),
    keyword         TEXT[]                         NOT NULL,
    web             VARCHAR(255),
    created         TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified        TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted         BOOLEAN       NOT NULL,
    system_id       UUID          NOT NULL,
    url             VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_raw_agendaitem PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_body
(
    id                  UUID          NOT NULL,
    short_name          VARCHAR(255),
    name                VARCHAR(500)  NOT NULL,
    website             VARCHAR(255),
    license             VARCHAR(255),
    license_valid_since TIMESTAMP(6) WITHOUT TIME ZONE,
    oparl_since         TIMESTAMP(6) WITHOUT TIME ZONE,
    ags                 VARCHAR(255),
    rgs                 VARCHAR(255),
    equivalent          TEXT[]                         NOT NULL,
    contact_email       VARCHAR(255),
    contact_name        VARCHAR(255),
    classification      VARCHAR(255),
    keyword             TEXT[]                         NOT NULL,
    web                 VARCHAR(255),
    created             TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified            TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted             BOOLEAN       NOT NULL,
    system_id           UUID          NOT NULL,
    url                 VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_raw_body PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_consultation
(
    id            UUID          NOT NULL,
    authoritative BOOLEAN,
    role          VARCHAR(255),
    license       VARCHAR(255),
    keyword       TEXT[]                         NOT NULL,
    web           VARCHAR(255),
    created       TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted       BOOLEAN       NOT NULL,
    system_id     UUID          NOT NULL,
    url           VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_raw_consultation PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_file
(
    id                   UUID          NOT NULL,
    name                 VARCHAR(500),
    file_name            VARCHAR(255),
    mime_type            VARCHAR(255),
    date                 TIMESTAMP(6) WITHOUT TIME ZONE,
    size                 INTEGER,
    sha1_checksum         VARCHAR(255),
    sha512_checksum       VARCHAR(255),
    text                 TEXT,
    access_url           VARCHAR(255),
    download_url         VARCHAR(255),
    external_service_url VARCHAR(255),
    file_license         VARCHAR(255),
    license              VARCHAR(255),
    keyword              TEXT[]                         NOT NULL,
    web                  VARCHAR(255),
    created              TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified             TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted              BOOLEAN       NOT NULL,
    system_id            UUID          NOT NULL,
    url                  VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_raw_file PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_legislative_term
(
    id         UUID          NOT NULL,
    name       VARCHAR(500),
    start_date TIMESTAMP(6) WITHOUT TIME ZONE,
    end_date   TIMESTAMP(6) WITHOUT TIME ZONE,
    license    VARCHAR(255),
    keyword    TEXT[]                         NOT NULL,
    web        VARCHAR(255),
    created    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted    BOOLEAN       NOT NULL,
    system_id  UUID          NOT NULL,
    url        VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_raw_legislativeterm PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_location
(
    id             UUID          NOT NULL,
    description    TEXT,
    geojson        JSONB,
    street_address VARCHAR(255),
    postal_code    VARCHAR(255),
    locality       VARCHAR(255),
    sub_locality   VARCHAR(255),
    room           VARCHAR(255),
    license        VARCHAR(255),
    keyword        TEXT[]                         NOT NULL,
    web            VARCHAR(255),
    created        TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified       TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted        BOOLEAN       NOT NULL,
    system_id      UUID          NOT NULL,
    url            VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_raw_location PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_meeting
(
    id            UUID          NOT NULL,
    name          VARCHAR(500),
    meeting_state VARCHAR(255),
    cancelled     BOOLEAN,
    start         TIMESTAMP(6) WITHOUT TIME ZONE,
    "end"         TIMESTAMP(6) WITHOUT TIME ZONE,
    license       VARCHAR(255),
    keyword       TEXT[]                         NOT NULL,
    web           VARCHAR(255),
    created       TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted       BOOLEAN       NOT NULL,
    system_id     UUID          NOT NULL,
    url           VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_raw_meeting PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_membership
(
    id           UUID          NOT NULL,
    role         VARCHAR(255),
    voting_right BOOLEAN,
    start_date   TIMESTAMP(6) WITHOUT TIME ZONE,
    end_date     TIMESTAMP(6) WITHOUT TIME ZONE,
    license      VARCHAR(255),
    keyword      TEXT[]                         NOT NULL,
    web          VARCHAR(255),
    created      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified     TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted      BOOLEAN       NOT NULL,
    system_id    UUID          NOT NULL,
    url          VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_raw_membership PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_organization
(
    id                UUID          NOT NULL,
    name              VARCHAR(500),
    short_name        VARCHAR(255),
    post              TEXT[]                         NOT NULL,
    organization_type VARCHAR(255),
    classification    VARCHAR(255),
    start_date        TIMESTAMP(6) WITHOUT TIME ZONE,
    end_date          TIMESTAMP(6) WITHOUT TIME ZONE,
    website           VARCHAR(255),
    license           VARCHAR(255),
    keyword           TEXT[]                         NOT NULL,
    web               VARCHAR(255),
    created           TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified          TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted           BOOLEAN       NOT NULL,
    system_id         UUID          NOT NULL,
    url               VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_raw_organization PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_paper
(
    id         UUID          NOT NULL,
    name       VARCHAR(500),
    reference  VARCHAR(255),
    date       TIMESTAMP(6) WITHOUT TIME ZONE,
    paper_type VARCHAR(255),
    license    VARCHAR(255),
    keyword    TEXT[]                         NOT NULL,
    web        VARCHAR(255),
    created    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted    BOOLEAN       NOT NULL,
    system_id  UUID          NOT NULL,
    url        VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_raw_paper PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_person
(
    id              UUID          NOT NULL,
    name            VARCHAR(500),
    family_name     VARCHAR(255),
    given_name      VARCHAR(255),
    form_of_address VARCHAR(255),
    affix           VARCHAR(255),
    title           TEXT[]                         NOT NULL,
    gender          VARCHAR(255),
    phone           TEXT[]                         NOT NULL,
    email           TEXT[]                         NOT NULL,
    status          TEXT[]                         NOT NULL,
    life            TEXT,
    life_source     VARCHAR(255),
    license         VARCHAR(255),
    keyword         TEXT[]                         NOT NULL,
    web             VARCHAR(255),
    created         TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified        TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted         BOOLEAN       NOT NULL,
    system_id       UUID          NOT NULL,
    url             VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_raw_person PRIMARY KEY (id)
);

CREATE TABLE oparl_raw_system
(
    id                   UUID          NOT NULL,
    oparl_version        VARCHAR(255)  NOT NULL,
    other_oparl_versions TEXT[]                         NOT NULL,
    license              VARCHAR(255),
    name                 VARCHAR(255)  NOT NULL,
    contact_email        VARCHAR(255),
    contact_name         VARCHAR(255),
    website              VARCHAR(255),
    vendor               VARCHAR(255),
    product              VARCHAR(255),
    web                  VARCHAR(255),
    created              TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified             TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted              BOOLEAN       NOT NULL,
    system_id            UUID          NOT NULL,
    url                  VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_raw_system PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_agenda_item
(
    id                 UUID          NOT NULL,
    number             VARCHAR(255),
    "order"            INTEGER,
    name               VARCHAR(500),
    public             BOOLEAN,
    result             VARCHAR(255),
    resolution_text    VARCHAR(255),
    start              TIMESTAMP(6) WITHOUT TIME ZONE,
    "end"              TIMESTAMP(6) WITHOUT TIME ZONE,
    license            VARCHAR(255),
    keyword            TEXT[]                         NOT NULL,
    web                VARCHAR(255),
    created            TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified           TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted            BOOLEAN       NOT NULL,
    body_id            UUID          NOT NULL,
    meeting_id         UUID          NOT NULL,
    resolution_file_id UUID,
    system_id          UUID          NOT NULL,
    url                VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_resolved_agendaitem PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_agenda_item_auxiliary_file
(
    agenda_item_id UUID NOT NULL,
    file_id        UUID NOT NULL
);

CREATE TABLE oparl_resolved_body
(
    id                  UUID          NOT NULL,
    short_name          VARCHAR(255),
    name                VARCHAR(500)  NOT NULL,
    website             VARCHAR(255),
    license             VARCHAR(255),
    license_valid_since TIMESTAMP(6) WITHOUT TIME ZONE,
    oparl_since         TIMESTAMP(6) WITHOUT TIME ZONE,
    ags                 VARCHAR(255),
    rgs                 VARCHAR(255),
    equivalent          TEXT[]                         NOT NULL,
    contact_email       VARCHAR(255),
    contact_name        VARCHAR(255),
    classification      VARCHAR(255),
    keyword             TEXT[]                         NOT NULL,
    web                 VARCHAR(255),
    created             TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified            TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted             BOOLEAN       NOT NULL,
    system_id           UUID          NOT NULL,
    url                 VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_resolved_body PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_consultation
(
    id             UUID          NOT NULL,
    authoritative  BOOLEAN,
    role           VARCHAR(255),
    license        VARCHAR(255),
    keyword        TEXT[]                         NOT NULL,
    web            VARCHAR(255),
    created        TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified       TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted        BOOLEAN       NOT NULL,
    body_id        UUID          NOT NULL,
    paper_id       UUID          NOT NULL,
    agenda_item_id UUID          NOT NULL,
    meeting_id     UUID          NOT NULL,
    system_id      UUID          NOT NULL,
    url            VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_resolved_consultation PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_consultation_organization
(
    consultation_id    UUID    NOT NULL,
    organization_id    UUID    NOT NULL,
    organization_order INTEGER NOT NULL,
    CONSTRAINT pk_oparl_resolved_consultation_organization PRIMARY KEY (consultation_id, organization_order)
);

CREATE TABLE oparl_resolved_file
(
    id                   UUID          NOT NULL,
    name                 VARCHAR(500),
    file_name            VARCHAR(255),
    mime_type            VARCHAR(255),
    date                 TIMESTAMP(6) WITHOUT TIME ZONE,
    size                 INTEGER,
    sha1checksum         VARCHAR(255),
    sha512checksum       VARCHAR(255),
    text                 TEXT,
    access_url           VARCHAR(255),
    download_url         VARCHAR(255),
    external_service_url VARCHAR(255),
    file_license         VARCHAR(255),
    license              VARCHAR(255),
    keyword              TEXT[]                         NOT NULL,
    web                  VARCHAR(255),
    created              TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified             TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted              BOOLEAN       NOT NULL,
    body_id              UUID          NOT NULL,
    system_id            UUID          NOT NULL,
    url                  VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_resolved_file PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_legislative_term
(
    id         UUID          NOT NULL,
    name       VARCHAR(500),
    start_date TIMESTAMP(6) WITHOUT TIME ZONE,
    end_date   TIMESTAMP(6) WITHOUT TIME ZONE,
    license    VARCHAR(255),
    keyword    TEXT[]                         NOT NULL,
    web        VARCHAR(255),
    created    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted    BOOLEAN       NOT NULL,
    body_id    UUID          NOT NULL,
    system_id  UUID          NOT NULL,
    url        VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_resolved_legislativeterm PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_location
(
    id             UUID          NOT NULL,
    description    TEXT,
    geojson        JSONB,
    street_address VARCHAR(255),
    postal_code    VARCHAR(255),
    locality       VARCHAR(255),
    sub_locality   VARCHAR(255),
    room           VARCHAR(255),
    license        VARCHAR(255),
    keyword        TEXT[]                         NOT NULL,
    web            VARCHAR(255),
    created        TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified       TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted        BOOLEAN       NOT NULL,
    body_id        UUID          NOT NULL,
    system_id      UUID          NOT NULL,
    url            VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_resolved_location PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_meeting
(
    id            UUID          NOT NULL,
    name          VARCHAR(500),
    meeting_state VARCHAR(255),
    cancelled     BOOLEAN,
    start         TIMESTAMP(6) WITHOUT TIME ZONE,
    "end"         TIMESTAMP(6) WITHOUT TIME ZONE,
    license       VARCHAR(255),
    keyword       TEXT[]                         NOT NULL,
    web           VARCHAR(255),
    created       TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified      TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted       BOOLEAN       NOT NULL,
    body_id       UUID          NOT NULL,
    system_id     UUID          NOT NULL,
    url           VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_resolved_meeting PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_meeting_organization
(
    meeting_id         UUID    NOT NULL,
    organization_id    UUID    NOT NULL,
    organization_order INTEGER NOT NULL,
    CONSTRAINT pk_oparl_resolved_meeting_organization PRIMARY KEY (meeting_id, organization_order)
);

CREATE TABLE oparl_resolved_membership
(
    id              UUID          NOT NULL,
    role            VARCHAR(255),
    voting_right    BOOLEAN,
    start_date      TIMESTAMP(6) WITHOUT TIME ZONE,
    end_date        TIMESTAMP(6) WITHOUT TIME ZONE,
    license         VARCHAR(255),
    keyword         TEXT[]                         NOT NULL,
    web             VARCHAR(255),
    created         TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified        TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted         BOOLEAN       NOT NULL,
    body_id         UUID          NOT NULL,
    person_id       UUID          NOT NULL,
    organization_id UUID          NOT NULL,
    on_behalf_of_id UUID,
    system_id       UUID          NOT NULL,
    url             VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_resolved_membership PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_organization
(
    id                UUID          NOT NULL,
    name              VARCHAR(500),
    short_name        VARCHAR(255),
    post              TEXT[]                         NOT NULL,
    organization_type VARCHAR(255),
    classification    VARCHAR(255),
    start_date        TIMESTAMP(6) WITHOUT TIME ZONE,
    end_date          TIMESTAMP(6) WITHOUT TIME ZONE,
    website           VARCHAR(255),
    license           VARCHAR(255),
    keyword           TEXT[]                         NOT NULL,
    web               VARCHAR(255),
    created           TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified          TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted           BOOLEAN       NOT NULL,
    body_id           UUID          NOT NULL,
    system_id         UUID          NOT NULL,
    url               VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_resolved_organization PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_paper
(
    id         UUID          NOT NULL,
    name       VARCHAR(500),
    reference  VARCHAR(255),
    date       TIMESTAMP(6) WITHOUT TIME ZONE,
    paper_type VARCHAR(255),
    license    VARCHAR(255),
    keyword    TEXT[]                         NOT NULL,
    web        VARCHAR(255),
    created    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted    BOOLEAN       NOT NULL,
    body_id    UUID          NOT NULL,
    system_id  UUID          NOT NULL,
    url        VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_resolved_paper PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_person
(
    id              UUID          NOT NULL,
    name            VARCHAR(500),
    family_name     VARCHAR(255),
    given_name      VARCHAR(255),
    form_of_address VARCHAR(255),
    affix           VARCHAR(255),
    title           TEXT[]                         NOT NULL,
    gender          VARCHAR(255),
    phone           TEXT[]                         NOT NULL,
    email           TEXT[]                         NOT NULL,
    status          TEXT[]                         NOT NULL,
    life            TEXT,
    life_source     VARCHAR(255),
    license         VARCHAR(255),
    keyword         TEXT[]                         NOT NULL,
    web             VARCHAR(255),
    created         TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified        TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted         BOOLEAN       NOT NULL,
    body_id         UUID          NOT NULL,
    system_id       UUID          NOT NULL,
    url             VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_resolved_person PRIMARY KEY (id)
);

CREATE TABLE oparl_resolved_system
(
    id                   UUID          NOT NULL,
    oparl_version        VARCHAR(255)  NOT NULL,
    other_oparl_versions TEXT[]                         NOT NULL,
    license              VARCHAR(255),
    name                 VARCHAR(255)  NOT NULL,
    contact_email        VARCHAR(255),
    contact_name         VARCHAR(255),
    website              VARCHAR(255),
    vendor               VARCHAR(255),
    product              VARCHAR(255),
    web                  VARCHAR(255),
    created              TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modified             TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    deleted              BOOLEAN       NOT NULL,
    system_id            UUID          NOT NULL,
    url                  VARCHAR(1024) NOT NULL,
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

ALTER TABLE oparl_resolved_body
    ADD CONSTRAINT uc_05f0fed013d5cdf75cd17ef27 UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_membership
    ADD CONSTRAINT uc_08e21c105de60f52a8db58e3a UNIQUE (system_id, url);

ALTER TABLE oparl_raw_meeting
    ADD CONSTRAINT uc_0beda6acac83501c8957db6a1 UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_file
    ADD CONSTRAINT uc_1930ac2f32a4ce7ed01960164 UNIQUE (system_id, url);

ALTER TABLE oparl_raw_legislative_term
    ADD CONSTRAINT uc_2463ef9313929e9cb5495a6be UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_organization
    ADD CONSTRAINT uc_2d567be64db56f8ce074248ff UNIQUE (system_id, url);

ALTER TABLE oparl_raw_system
    ADD CONSTRAINT uc_2e3345c92eff7c2763e6c55e8 UNIQUE (system_id, url);

ALTER TABLE oparl_raw_file
    ADD CONSTRAINT uc_3b68422568ceb9145bfda57b6 UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_system
    ADD CONSTRAINT uc_3c966ea0398f1df33788ca791 UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_legislative_term
    ADD CONSTRAINT uc_6c27c97270f5c1fd5363e0e18 UNIQUE (system_id, url);

ALTER TABLE oparl_raw_consultation
    ADD CONSTRAINT uc_7f374d79fadc8d203946632c4 UNIQUE (system_id, url);

ALTER TABLE oparl_raw_body
    ADD CONSTRAINT uc_863c9ec8c2149e19387cbda45 UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_paper
    ADD CONSTRAINT uc_8fb4ab67519b55623c099c1b2 UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_consultation
    ADD CONSTRAINT uc_92992093722989385beb5acb5 UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_meeting
    ADD CONSTRAINT uc_a1a88e719c890632458fd0faf UNIQUE (system_id, url);

ALTER TABLE oparl_raw_organization
    ADD CONSTRAINT uc_b110501c830535bf9cdca8e25 UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_location
    ADD CONSTRAINT uc_b6fed2d0c395a1cdf5172fda2 UNIQUE (system_id, url);

ALTER TABLE oparl_raw_agenda_item
    ADD CONSTRAINT uc_c3e281ca09cd8c145ceb85429 UNIQUE (system_id, url);

ALTER TABLE oparl_raw_paper
    ADD CONSTRAINT uc_cf4e19bd058e6c04238c08c23 UNIQUE (system_id, url);

ALTER TABLE oparl_raw_person
    ADD CONSTRAINT uc_d3d1c4116fffae88d17db9096 UNIQUE (system_id, url);

ALTER TABLE oparl_raw_membership
    ADD CONSTRAINT uc_dbef12fe47031d19b9422a7d4 UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_agenda_item
    ADD CONSTRAINT uc_e0fbd1c12d7b7e49852a45b1a UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_person
    ADD CONSTRAINT uc_e9019b201f964b1d82f8d4da3 UNIQUE (system_id, url);

ALTER TABLE oparl_raw_location
    ADD CONSTRAINT uc_f38d4c3317f42662b71211430 UNIQUE (system_id, url);

ALTER TABLE oparl_resolved_consultation
    ADD CONSTRAINT uc_oparl_resolved_consultation_agenda_item UNIQUE (agenda_item_id);

ALTER TABLE oparl_raw_agenda_item
    ADD CONSTRAINT FK_OPARL_RAW_AGENDAITEM_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_raw_body
    ADD CONSTRAINT FK_OPARL_RAW_BODY_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_raw_consultation
    ADD CONSTRAINT FK_OPARL_RAW_CONSULTATION_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_raw_file
    ADD CONSTRAINT FK_OPARL_RAW_FILE_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_raw_legislative_term
    ADD CONSTRAINT FK_OPARL_RAW_LEGISLATIVETERM_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_raw_location
    ADD CONSTRAINT FK_OPARL_RAW_LOCATION_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_raw_meeting
    ADD CONSTRAINT FK_OPARL_RAW_MEETING_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_raw_membership
    ADD CONSTRAINT FK_OPARL_RAW_MEMBERSHIP_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_raw_organization
    ADD CONSTRAINT FK_OPARL_RAW_ORGANIZATION_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_raw_paper
    ADD CONSTRAINT FK_OPARL_RAW_PAPER_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_raw_person
    ADD CONSTRAINT FK_OPARL_RAW_PERSON_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_raw_system
    ADD CONSTRAINT FK_OPARL_RAW_SYSTEM_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_agenda_item
    ADD CONSTRAINT FK_OPARL_RESOLVED_AGENDAITEM_ON_BODY FOREIGN KEY (body_id) REFERENCES oparl_resolved_body (id);

ALTER TABLE oparl_resolved_agenda_item
    ADD CONSTRAINT FK_OPARL_RESOLVED_AGENDAITEM_ON_MEETING FOREIGN KEY (meeting_id) REFERENCES oparl_resolved_meeting (id);

ALTER TABLE oparl_resolved_agenda_item
    ADD CONSTRAINT FK_OPARL_RESOLVED_AGENDAITEM_ON_RESOLUTION_FILE FOREIGN KEY (resolution_file_id) REFERENCES oparl_resolved_file (id);

ALTER TABLE oparl_resolved_agenda_item
    ADD CONSTRAINT FK_OPARL_RESOLVED_AGENDAITEM_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_body
    ADD CONSTRAINT FK_OPARL_RESOLVED_BODY_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_consultation
    ADD CONSTRAINT FK_OPARL_RESOLVED_CONSULTATION_ON_AGENDA_ITEM FOREIGN KEY (agenda_item_id) REFERENCES oparl_resolved_agenda_item (id);

ALTER TABLE oparl_resolved_consultation
    ADD CONSTRAINT FK_OPARL_RESOLVED_CONSULTATION_ON_BODY FOREIGN KEY (body_id) REFERENCES oparl_resolved_body (id);

ALTER TABLE oparl_resolved_consultation
    ADD CONSTRAINT FK_OPARL_RESOLVED_CONSULTATION_ON_MEETING FOREIGN KEY (meeting_id) REFERENCES oparl_resolved_meeting (id);

ALTER TABLE oparl_resolved_consultation
    ADD CONSTRAINT FK_OPARL_RESOLVED_CONSULTATION_ON_PAPER FOREIGN KEY (paper_id) REFERENCES oparl_resolved_paper (id);

ALTER TABLE oparl_resolved_consultation
    ADD CONSTRAINT FK_OPARL_RESOLVED_CONSULTATION_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_file
    ADD CONSTRAINT FK_OPARL_RESOLVED_FILE_ON_BODY FOREIGN KEY (body_id) REFERENCES oparl_resolved_body (id);

ALTER TABLE oparl_resolved_file
    ADD CONSTRAINT FK_OPARL_RESOLVED_FILE_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_legislative_term
    ADD CONSTRAINT FK_OPARL_RESOLVED_LEGISLATIVETERM_ON_BODY FOREIGN KEY (body_id) REFERENCES oparl_resolved_body (id);

ALTER TABLE oparl_resolved_legislative_term
    ADD CONSTRAINT FK_OPARL_RESOLVED_LEGISLATIVETERM_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_location
    ADD CONSTRAINT FK_OPARL_RESOLVED_LOCATION_ON_BODY FOREIGN KEY (body_id) REFERENCES oparl_resolved_body (id);

ALTER TABLE oparl_resolved_location
    ADD CONSTRAINT FK_OPARL_RESOLVED_LOCATION_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_meeting
    ADD CONSTRAINT FK_OPARL_RESOLVED_MEETING_ON_BODY FOREIGN KEY (body_id) REFERENCES oparl_resolved_body (id);

ALTER TABLE oparl_resolved_meeting
    ADD CONSTRAINT FK_OPARL_RESOLVED_MEETING_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_membership
    ADD CONSTRAINT FK_OPARL_RESOLVED_MEMBERSHIP_ON_BODY FOREIGN KEY (body_id) REFERENCES oparl_resolved_body (id);

ALTER TABLE oparl_resolved_membership
    ADD CONSTRAINT FK_OPARL_RESOLVED_MEMBERSHIP_ON_ON_BEHALF_OF FOREIGN KEY (on_behalf_of_id) REFERENCES oparl_resolved_organization (id);

ALTER TABLE oparl_resolved_membership
    ADD CONSTRAINT FK_OPARL_RESOLVED_MEMBERSHIP_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES oparl_resolved_organization (id);

ALTER TABLE oparl_resolved_membership
    ADD CONSTRAINT FK_OPARL_RESOLVED_MEMBERSHIP_ON_PERSON FOREIGN KEY (person_id) REFERENCES oparl_resolved_person (id);

ALTER TABLE oparl_resolved_membership
    ADD CONSTRAINT FK_OPARL_RESOLVED_MEMBERSHIP_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_organization
    ADD CONSTRAINT FK_OPARL_RESOLVED_ORGANIZATION_ON_BODY FOREIGN KEY (body_id) REFERENCES oparl_resolved_body (id);

ALTER TABLE oparl_resolved_organization
    ADD CONSTRAINT FK_OPARL_RESOLVED_ORGANIZATION_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_paper
    ADD CONSTRAINT FK_OPARL_RESOLVED_PAPER_ON_BODY FOREIGN KEY (body_id) REFERENCES oparl_resolved_body (id);

ALTER TABLE oparl_resolved_paper
    ADD CONSTRAINT FK_OPARL_RESOLVED_PAPER_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_person
    ADD CONSTRAINT FK_OPARL_RESOLVED_PERSON_ON_BODY FOREIGN KEY (body_id) REFERENCES oparl_resolved_body (id);

ALTER TABLE oparl_resolved_person
    ADD CONSTRAINT FK_OPARL_RESOLVED_PERSON_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_system
    ADD CONSTRAINT FK_OPARL_RESOLVED_SYSTEM_ON_SYSTEM FOREIGN KEY (system_id) REFERENCES oparl_system (id);

ALTER TABLE oparl_resolved_agenda_item_auxiliary_file
    ADD CONSTRAINT fk_oparesageiteauxfil_on_resolved_agenda_item FOREIGN KEY (agenda_item_id) REFERENCES oparl_resolved_agenda_item (id);

ALTER TABLE oparl_resolved_agenda_item_auxiliary_file
    ADD CONSTRAINT fk_oparesageiteauxfil_on_resolved_file FOREIGN KEY (file_id) REFERENCES oparl_resolved_file (id);

ALTER TABLE oparl_resolved_consultation_organization
    ADD CONSTRAINT fk_oparesconorg_on_resolved_consultation FOREIGN KEY (consultation_id) REFERENCES oparl_resolved_consultation (id);

ALTER TABLE oparl_resolved_consultation_organization
    ADD CONSTRAINT fk_oparesconorg_on_resolved_organization FOREIGN KEY (organization_id) REFERENCES oparl_resolved_organization (id);

ALTER TABLE oparl_resolved_meeting_organization
    ADD CONSTRAINT fk_oparesmeeorg_on_resolved_meeting FOREIGN KEY (meeting_id) REFERENCES oparl_resolved_meeting (id);

ALTER TABLE oparl_resolved_meeting_organization
    ADD CONSTRAINT fk_oparesmeeorg_on_resolved_organization FOREIGN KEY (organization_id) REFERENCES oparl_resolved_organization (id);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);