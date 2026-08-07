ALTER TABLE oparl_raw_legislative_term
    ADD COLUMN body_url VARCHAR(1024);

ALTER TABLE oparl_raw_organization
    ADD COLUMN body_url VARCHAR(1024);

ALTER TABLE oparl_raw_paper
    ADD COLUMN body_url VARCHAR(1024);

ALTER TABLE oparl_raw_person
    ADD COLUMN body_url VARCHAR(1024);
