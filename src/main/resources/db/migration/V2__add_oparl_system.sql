CREATE TABLE oparl_system
(
    id  UUID          NOT NULL,
    url VARCHAR(1024) NOT NULL,
    CONSTRAINT pk_oparl_system PRIMARY KEY (id)
);

WITH imported_system_ids AS (
    SELECT system_id FROM oparl_raw_agenda_item
    UNION SELECT system_id FROM oparl_raw_body
    UNION SELECT system_id FROM oparl_raw_consultation
    UNION SELECT system_id FROM oparl_raw_file
    UNION SELECT system_id FROM oparl_raw_legislative_term
    UNION SELECT system_id FROM oparl_raw_location
    UNION SELECT system_id FROM oparl_raw_meeting
    UNION SELECT system_id FROM oparl_raw_membership
    UNION SELECT system_id FROM oparl_raw_organization
    UNION SELECT system_id FROM oparl_raw_paper
    UNION SELECT system_id FROM oparl_raw_person
    UNION SELECT system_id FROM oparl_raw_system
    UNION SELECT system_id FROM oparl_resolved_agenda_item
    UNION SELECT system_id FROM oparl_resolved_body
    UNION SELECT system_id FROM oparl_resolved_consultation
    UNION SELECT system_id FROM oparl_resolved_file
    UNION SELECT system_id FROM oparl_resolved_legislative_term
    UNION SELECT system_id FROM oparl_resolved_location
    UNION SELECT system_id FROM oparl_resolved_meeting
    UNION SELECT system_id FROM oparl_resolved_membership
    UNION SELECT system_id FROM oparl_resolved_organization
    UNION SELECT system_id FROM oparl_resolved_paper
    UNION SELECT system_id FROM oparl_resolved_person
    UNION SELECT system_id FROM oparl_resolved_system
), system_resource_urls AS (
    SELECT system_id, url FROM oparl_raw_system
    UNION ALL
    SELECT system_id, url FROM oparl_resolved_system
)
INSERT INTO oparl_system (id, url)
SELECT imported_system_ids.system_id,
       COALESCE(MIN(system_resource_urls.url), 'urn:openbert:oparl-system:' || imported_system_ids.system_id)
FROM imported_system_ids
LEFT JOIN system_resource_urls USING (system_id)
GROUP BY imported_system_ids.system_id;

ALTER TABLE oparl_raw_agenda_item DROP CONSTRAINT uc_oparl_raw_agendaitem_url;
ALTER TABLE oparl_raw_body DROP CONSTRAINT uc_oparl_raw_body_url;
ALTER TABLE oparl_raw_consultation DROP CONSTRAINT uc_oparl_raw_consultation_url;
ALTER TABLE oparl_raw_file DROP CONSTRAINT uc_oparl_raw_file_url;
ALTER TABLE oparl_raw_legislative_term DROP CONSTRAINT uc_oparl_raw_legislativeterm_url;
ALTER TABLE oparl_raw_location DROP CONSTRAINT uc_oparl_raw_location_url;
ALTER TABLE oparl_raw_meeting DROP CONSTRAINT uc_oparl_raw_meeting_url;
ALTER TABLE oparl_raw_membership DROP CONSTRAINT uc_oparl_raw_membership_url;
ALTER TABLE oparl_raw_organization DROP CONSTRAINT uc_oparl_raw_organization_url;
ALTER TABLE oparl_raw_paper DROP CONSTRAINT uc_oparl_raw_paper_url;
ALTER TABLE oparl_raw_person DROP CONSTRAINT uc_oparl_raw_person_url;
ALTER TABLE oparl_raw_system DROP CONSTRAINT uc_oparl_raw_system_url;
ALTER TABLE oparl_resolved_agenda_item DROP CONSTRAINT uc_oparl_resolved_agendaitem_url;
ALTER TABLE oparl_resolved_body DROP CONSTRAINT uc_oparl_resolved_body_url;
ALTER TABLE oparl_resolved_consultation DROP CONSTRAINT uc_oparl_resolved_consultation_url;
ALTER TABLE oparl_resolved_file DROP CONSTRAINT uc_oparl_resolved_file_url;
ALTER TABLE oparl_resolved_legislative_term DROP CONSTRAINT uc_oparl_resolved_legislativeterm_url;
ALTER TABLE oparl_resolved_location DROP CONSTRAINT uc_oparl_resolved_location_url;
ALTER TABLE oparl_resolved_meeting DROP CONSTRAINT uc_oparl_resolved_meeting_url;
ALTER TABLE oparl_resolved_membership DROP CONSTRAINT uc_oparl_resolved_membership_url;
ALTER TABLE oparl_resolved_organization DROP CONSTRAINT uc_oparl_resolved_organization_url;
ALTER TABLE oparl_resolved_paper DROP CONSTRAINT uc_oparl_resolved_paper_url;
ALTER TABLE oparl_resolved_person DROP CONSTRAINT uc_oparl_resolved_person_url;
ALTER TABLE oparl_resolved_system DROP CONSTRAINT uc_oparl_resolved_system_url;

ALTER TABLE oparl_raw_agenda_item ADD CONSTRAINT fk_oparl_raw_agendaitem_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_raw_body ADD CONSTRAINT fk_oparl_raw_body_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_raw_consultation ADD CONSTRAINT fk_oparl_raw_consultation_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_raw_file ADD CONSTRAINT fk_oparl_raw_file_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_raw_legislative_term ADD CONSTRAINT fk_oparl_raw_legislativeterm_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_raw_location ADD CONSTRAINT fk_oparl_raw_location_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_raw_meeting ADD CONSTRAINT fk_oparl_raw_meeting_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_raw_membership ADD CONSTRAINT fk_oparl_raw_membership_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_raw_organization ADD CONSTRAINT fk_oparl_raw_organization_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_raw_paper ADD CONSTRAINT fk_oparl_raw_paper_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_raw_person ADD CONSTRAINT fk_oparl_raw_person_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_raw_system ADD CONSTRAINT fk_oparl_raw_system_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_resolved_agenda_item ADD CONSTRAINT fk_oparl_resolved_agendaitem_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_resolved_body ADD CONSTRAINT fk_oparl_resolved_body_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_resolved_consultation ADD CONSTRAINT fk_oparl_resolved_consultation_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_resolved_file ADD CONSTRAINT fk_oparl_resolved_file_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_resolved_legislative_term ADD CONSTRAINT fk_oparl_resolved_legislativeterm_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_resolved_location ADD CONSTRAINT fk_oparl_resolved_location_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_resolved_meeting ADD CONSTRAINT fk_oparl_resolved_meeting_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_resolved_membership ADD CONSTRAINT fk_oparl_resolved_membership_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_resolved_organization ADD CONSTRAINT fk_oparl_resolved_organization_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_resolved_paper ADD CONSTRAINT fk_oparl_resolved_paper_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_resolved_person ADD CONSTRAINT fk_oparl_resolved_person_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);
ALTER TABLE oparl_resolved_system ADD CONSTRAINT fk_oparl_resolved_system_system FOREIGN KEY (system_id) REFERENCES oparl_system (id);