ALTER TABLE oparl_raw_agenda_item
    ADD COLUMN meeting_url VARCHAR(1024),
    ADD COLUMN consultation_url VARCHAR(1024),
    ADD COLUMN resolution_file_url VARCHAR(1024),
    ADD COLUMN auxiliary_file_urls TEXT[] NOT NULL DEFAULT '{}';

ALTER TABLE oparl_raw_consultation
    ADD COLUMN paper_url VARCHAR(1024),
    ADD COLUMN agenda_item_url VARCHAR(1024),
    ADD COLUMN meeting_url VARCHAR(1024),
    ADD COLUMN organization_urls TEXT[] NOT NULL DEFAULT '{}';

ALTER TABLE oparl_raw_file
    ADD COLUMN meeting_urls TEXT[] NOT NULL DEFAULT '{}',
    ADD COLUMN agenda_item_urls TEXT[] NOT NULL DEFAULT '{}',
    ADD COLUMN paper_urls TEXT[] NOT NULL DEFAULT '{}';

ALTER TABLE oparl_raw_location
    ADD COLUMN body_urls TEXT[] NOT NULL DEFAULT '{}',
    ADD COLUMN organization_urls TEXT[] NOT NULL DEFAULT '{}',
    ADD COLUMN person_urls TEXT[] NOT NULL DEFAULT '{}',
    ADD COLUMN meeting_urls TEXT[] NOT NULL DEFAULT '{}',
    ADD COLUMN paper_urls TEXT[] NOT NULL DEFAULT '{}';

ALTER TABLE oparl_raw_meeting
    ADD COLUMN organization_urls TEXT[] NOT NULL DEFAULT '{}',
    ADD COLUMN participant_urls TEXT[] NOT NULL DEFAULT '{}';

ALTER TABLE oparl_raw_membership
    ADD COLUMN person_url VARCHAR(1024),
    ADD COLUMN organization_url VARCHAR(1024),
    ADD COLUMN on_behalf_of_url VARCHAR(1024);
