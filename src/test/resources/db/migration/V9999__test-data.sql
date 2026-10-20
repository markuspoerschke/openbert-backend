-- OParl system
INSERT INTO oparl_system (id, url)
VALUES ('9352fa21-68c6-4f4e-8832-08a0454ff179', 'https://oparl-demo.example.org');

-- Resolved OParl system
INSERT INTO public.oparl_resolved_system (id, oparl_version, other_oparl_versions, license, name, contact_email,
                                          contact_name, website, vendor, product, web, system_id, url, created,
                                          modified, deleted)
VALUES ('44444444-4444-4444-a444-444444444444', 'https://schema.oparl.org/1.1/', '{}', null,
        'OpenBERT demo search system', 'info@oparl-demo.example.org', 'OpenBERT Team',
        'https://oparl-demo.example.org', null, null, null, '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/system',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- A second source system must not be represented by the same local System ID in search.
INSERT INTO public.oparl_resolved_system
    (id, oparl_version, other_oparl_versions, name, system_id, url, created, modified, deleted)
VALUES ('55555555-5555-4555-a555-555555555555', 'https://schema.oparl.org/1.1/', '{}',
        'Another demo search system', '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://second-oparl.example.org/oparl/v1.1/system',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- An unreferenced crawl target is retained for provenance but is not due
INSERT INTO crawl_target (id, system_id, url, entity_type, created_at, last_crawled_at, modified_at,
                          reference_count, scheduled_at, job_id)
VALUES ('eeeeeeee-eeee-4eee-aeee-eeeeeeeeeeee', '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/location/inline', 'LOCATION', '2026-07-22 14:30:55.000000', null,
        '2026-07-22 14:30:55.000000', 0, null, null);

-- Due crawl targets use deliberately inverted reference counts to verify dependency ordering wins
INSERT INTO crawl_target (id, system_id, url, entity_type, created_at, modified_at, reference_count)
VALUES ('30000000-0000-4000-8000-000000000001', '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/organization/due', 'ORGANIZATION',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', 1),
       ('30000000-0000-4000-8000-000000000002', '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/person/due', 'PERSON',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', 10),
       ('30000000-0000-4000-8000-000000000003', '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/membership/due', 'MEMBERSHIP',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', 100);

-- OParl body
INSERT INTO public.oparl_resolved_body (id, short_name, name, website, license, license_valid_since, oparl_since, ags,
                                        rgs, equivalent, contact_email, contact_name, classification, keyword, web,
                                        system_id, url, created, modified, deleted)
VALUES ('903c26aa-dc8f-4d34-afd6-afdc5040dbd3', 'Neu Offenstadt', 'Neu Offenstadt – Stadtparlament',
        'https://neuoffenstadt.example.com', null, null, '1999-07-22 14:25:03.000000', null, null, '{}', null, null,
        null, '{}', null, '9352fa21-68c6-4f4e-8832-08a0454ff179', 'https://oparl-demo.example.org/oparl/v1.1/body/123',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- A second OParl body verifies that body-scoped resources cannot be retrieved through another body
INSERT INTO public.oparl_resolved_body (id, short_name, name, website, license, license_valid_since, oparl_since, ags,
                                        rgs, equivalent, contact_email, contact_name, classification, keyword, web,
                                        system_id, url, created, modified, deleted)
VALUES ('ffffffff-ffff-4fff-afff-ffffffffffff', 'Anderstadt', 'Anderstadt – Stadtparlament',
        'https://anderstadt.example.com', null, null, '1999-07-22 14:25:03.000000', null, null, '{}', null, null,
        null, '{}', null, '9352fa21-68c6-4f4e-8832-08a0454ff179', 'https://oparl-demo.example.org/oparl/v1.1/body/456',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- OParl legislative term
INSERT INTO public.oparl_resolved_legislative_term (id, body_id, name, start_date, end_date, license, keyword, web,
                                                    system_id, url, created, modified, deleted)
VALUES ('20ae1f2f-991a-4ea1-a546-27869c8c542a', '903c26aa-dc8f-4d34-afd6-afdc5040dbd3',
        '21. Wahlperiode', '2025-11-01 00:00:00.000000', '2030-10-31 00:00:00.000000', 'CC0-1.0',
        '{"wahlperiode","stadtrat"}', 'https://neuoffenstadt.example.com/rat/wahlperioden/21',
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/legislativeterm/21',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- OParl location
INSERT INTO public.oparl_resolved_location (id, body_id, description, geojson, street_address, postal_code, locality,
                                            sub_locality, room, license, keyword, web, system_id, url, created, modified,
                                            deleted)
VALUES ('a70c1459-c98f-4e9c-a02f-4bb21b837c04', '903c26aa-dc8f-4d34-afd6-afdc5040dbd3',
        'Rathaus Neu Offenstadt, Rathausplatz 1, 12345 Neu Offenstadt',
        '{"type":"Feature","geometry":{"type":"Point","coordinates":[50.1234,10.4321]},"properties":{"name":"Rathausplatz"}}',
        'Rathausplatz 1', '12345', 'Neu Offenstadt', 'Innenstadt', 'Ratssaal', 'CC0-1.0',
        '{"rathaus","sitzungsort"}', 'https://neuoffenstadt.example.com/rathaus',
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/location/1',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- OParl file
INSERT INTO public.oparl_resolved_file (id, body_id, name, file_name, mime_type, date, size, sha1_checksum,
                                        sha512_checksum, text, access_url, download_url, external_service_url,
                                        file_license, license, keyword, web, system_id, url, created, modified, deleted)
VALUES ('3d663f77-05ca-4a14-b788-68ad340ea271', '903c26aa-dc8f-4d34-afd6-afdc5040dbd3',
        'Mock council document', 'mock-council-document.txt', 'text/plain', '2026-07-20 00:00:00.000000', 87, null,
        '3ba3e94660704f7a18c3933a0a50bc401a78ccf39654ce093c7947d2ad252b9a675ad7a8ce9dcd7a2b61631fda9d8605bb4853a3509886ce348517f103f4f099',
        'Placeholder for a council document.', 'https://oparl-demo.example.org/files/1',
        'https://oparl-demo.example.org/files/download/1', null, 'https://creativecommons.org/publicdomain/zero/1.0/',
        'CC0-1.0', '{"mock","council document"}', 'https://neuoffenstadt.example.com/documents/1',
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/file/1',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- Search fixtures belong to the second body so the existing first-body file list remains stable
INSERT INTO public.oparl_resolved_file (id, body_id, name, file_name, mime_type, date, size, sha1_checksum,
                                        sha512_checksum, text, access_url, download_url, external_service_url,
                                        file_license, license, keyword, web, system_id, url, created, modified, deleted)
VALUES ('22222222-2222-4222-a222-222222222222', 'ffffffff-ffff-4fff-afff-ffffffffffff',
        'Mobility plan', 'mobility-plan.txt', 'text/plain', '2026-07-21 00:00:00.000000', 48, null, null,
        'A searchable document about public transport.', 'https://oparl-demo.example.org/files/2', null, null,
        null, 'CC0-1.0', '{"mobility"}', null, '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/file/2',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false),
       ('33333333-3333-4333-a333-333333333333', 'ffffffff-ffff-4fff-afff-ffffffffffff',
        'Park plan', 'park-plan.txt', 'text/plain', '2026-07-22 00:00:00.000000', 44, null, null,
        'A searchable document about the city park.', 'https://oparl-demo.example.org/files/3', null, null,
        null, 'CC0-1.0', ARRAY['park', 'budget%2026', 'budget_2026', 'budget\2026', 'budget!2026'], null, '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/file/3',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- A tombstone retaining matching text must not affect search results or pagination.
INSERT INTO public.oparl_resolved_file
    (id, body_id, name, text, keyword, system_id, url, created, modified, deleted)
VALUES ('66666666-6666-4666-a666-666666666666', 'ffffffff-ffff-4fff-afff-ffffffffffff',
        'Deleted mobility plan', 'A searchable document about public transport.', '{}',
        '9352fa21-68c6-4f4e-8832-08a0454ff179', 'https://oparl-demo.example.org/oparl/v1.1/file/deleted',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', true);

-- Raw OParl file with extracted text longer than VARCHAR(255)
INSERT INTO public.oparl_raw_file (id, name, text, paper_urls, keyword, created, modified, deleted, system_id, url)
VALUES ('aaaaaaaa-aaaa-4aaa-aaaa-aaaaaaaaaaaa', repeat('Long OParl file name. ', 20),
        repeat('Extracted OParl document content. ', 20),
        '{"https://oparl-demo.example.org/oparl/v1.1/paper/42"}', '{}',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false,
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/file/long-text');

-- Raw OParl entities retain their body references as URLs until relationships are resolved
INSERT INTO public.oparl_raw_legislative_term
    (id, body_url, keyword, created, modified, deleted, system_id, url)
VALUES ('10000000-0000-4000-8000-000000000001',
        'https://oparl-demo.example.org/oparl/v1.1/body/123', '{}',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false,
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/legislativeterm/unresolved');

-- A completed resolve queue item is retained as processing provenance
INSERT INTO resolve_target
    (id, raw_entity_id, entity_type, created_at, last_resolved_at, modified_at, reference_count, scheduled_at, job_id)
VALUES ('20000000-0000-4000-8000-000000000001', '10000000-0000-4000-8000-000000000001', 'LEGISLATIVE_TERM',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000',
        0, null, null);

INSERT INTO public.oparl_raw_organization
    (id, body_url, post, keyword, created, modified, deleted, system_id, url)
VALUES ('10000000-0000-4000-8000-000000000002',
        'https://oparl-demo.example.org/oparl/v1.1/body/123', '{}', '{}',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false,
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/organization/unresolved');

INSERT INTO public.oparl_raw_paper
    (id, body_url, keyword, created, modified, deleted, system_id, url)
VALUES ('10000000-0000-4000-8000-000000000003',
        'https://oparl-demo.example.org/oparl/v1.1/body/123', '{}',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false,
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/paper/unresolved');

INSERT INTO public.oparl_raw_person
    (id, body_url, title, phone, email, status, keyword, created, modified, deleted, system_id, url)
VALUES ('10000000-0000-4000-8000-000000000004',
        'https://oparl-demo.example.org/oparl/v1.1/body/123', '{}', '{}', '{}', '{}', '{}',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false,
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/person/unresolved');

-- OParl paper
INSERT INTO public.oparl_resolved_paper (id, body_id, name, reference, date, paper_type, license, keyword, web,
                                         system_id, url, created, modified, deleted)
VALUES ('f6d47f4c-e5db-4dab-91f8-3bafc825d30c', '903c26aa-dc8f-4d34-afd6-afdc5040dbd3',
        'Haushaltssatzung für das Jahr 2027', 'V-2026-42', '2026-07-21 00:00:00.000000', 'Beschlussvorlage',
        'CC0-1.0', '{"haushalt","finanzen"}', 'https://neuoffenstadt.example.com/drucksachen/V-2026-42',
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/paper/42',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- OParl meeting
INSERT INTO public.oparl_resolved_meeting (id, body_id, name, meeting_state, cancelled, start, "end", license, keyword,
                                           web, system_id, url, created, modified, deleted)
VALUES ('1dd8b01e-a39c-4b40-9f24-46f7a42a3795', '903c26aa-dc8f-4d34-afd6-afdc5040dbd3',
        '4. Sitzung des Finanzausschusses', 'durchgeführt', false, '2026-07-24 08:00:00.000000',
        '2026-07-24 12:00:00.000000', 'CC0-1.0', '{"finanzen","haushalt"}',
        'https://neuoffenstadt.example.com/sitzungen/finanzausschuss-4',
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/meeting/281',
        '2026-07-22 14:30:55.000000', '2026-07-24 13:05:27.000000', false);

-- OParl agenda item
INSERT INTO public.oparl_resolved_agenda_item (id, body_id, meeting_id, resolution_file_id, number, "order", name,
                                               public, result, resolution_text, start, "end", license, keyword, web,
                                               system_id, url, created, modified, deleted)
VALUES ('7b728a96-ae9a-40d1-b010-cc909ab2dd87', '903c26aa-dc8f-4d34-afd6-afdc5040dbd3',
        '1dd8b01e-a39c-4b40-9f24-46f7a42a3795', '3d663f77-05ca-4a14-b788-68ad340ea271', '10.1', 0,
        'Haushaltssatzung für das Jahr 2027', true, 'Geändert beschlossen',
        'Der Haushaltsansatz wurde entsprechend der Beratung angepasst.',
        '2026-07-24 09:15:00.000000', '2026-07-24 10:00:00.000000', 'CC0-1.0',
        '{"haushalt","beschluss"}', 'https://neuoffenstadt.example.com/sitzungen/finanzausschuss-4/top-10-1',
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/agendaitem/3271',
        '2026-07-22 14:30:55.000000', '2026-07-24 13:05:27.000000', false);

INSERT INTO public.oparl_resolved_agenda_item_auxiliary_file (agenda_item_id, file_id)
VALUES ('7b728a96-ae9a-40d1-b010-cc909ab2dd87', '3d663f77-05ca-4a14-b788-68ad340ea271');

-- A second agenda item verifies that Meeting.agendaItem follows agenda order rather than UUID order
INSERT INTO public.oparl_resolved_agenda_item (id, body_id, meeting_id, resolution_file_id, number, "order", name,
                                               public, result, resolution_text, start, "end", license, keyword, web,
                                               system_id, url, created, modified, deleted)
VALUES ('11111111-1111-4111-a111-111111111111', '903c26aa-dc8f-4d34-afd6-afdc5040dbd3',
        '1dd8b01e-a39c-4b40-9f24-46f7a42a3795', null, '10.2', 1,
        'Investitionsprogramm 2027', true, 'Zur Kenntnis genommen', null,
        '2026-07-24 10:00:00.000000', '2026-07-24 10:30:00.000000', 'CC0-1.0',
        '{"investitionen"}', 'https://neuoffenstadt.example.com/sitzungen/finanzausschuss-4/top-10-2',
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/agendaitem/3272',
        '2026-07-22 14:30:55.000000', '2026-07-24 13:05:27.000000', false);

-- An unresolved agenda item must not be exposed by the public API
INSERT INTO public.oparl_raw_agenda_item (id, meeting_url, resolution_file_url, auxiliary_file_urls, number, "order",
                                          name, public, result, resolution_text, start, "end", license, keyword, web,
                                          system_id, url, created, modified, deleted)
VALUES ('0b30e64f-a824-4856-903f-63a81448fb70',
        'https://oparl-demo.example.org/oparl/v1.1/meeting/999',
        'https://oparl-demo.example.org/oparl/v1.1/file/1',
        '{"https://oparl-demo.example.org/oparl/v1.1/file/1"}',
        '11', 1, 'Noch nicht aufgelöster Tagesordnungspunkt', true,
        null, null, null, null, null, '{}', null, '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/agendaitem/999',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- An unresolved meeting must not be exposed by the public API
INSERT INTO public.oparl_raw_meeting (id, organization_urls, name, meeting_state, cancelled, start, "end", license,
                                      keyword, web, system_id, url, created, modified, deleted)
VALUES ('38e8985b-a504-4c24-bd18-0953fb593350',
        '{"https://oparl-demo.example.org/oparl/v1.1/organization/34"}',
        'Noch nicht aufgelöste Sitzung', 'terminiert', false,
        '2026-08-01 10:00:00.000000', null, null, '{}', null, '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/meeting/999',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- OParl organization
INSERT INTO public.oparl_resolved_organization (id, body_id, name, short_name, post, organization_type, classification,
                                                start_date, end_date, website, license, keyword, web, system_id, url,
                                                created, modified, deleted)
VALUES ('9f7d37f9-cd93-4b11-a868-19a6c1e39318', '903c26aa-dc8f-4d34-afd6-afdc5040dbd3',
        'Ausschuss für Haushalt und Finanzen', 'Finanzausschuss', '{"Vorsitz","Mitglied"}', 'Gremium', 'Ausschuss',
        '2012-07-17 00:00:00.000000', null, 'https://finanzen.neuoffenstadt.example.com', 'CC0-1.0',
        '{"finanzen","haushalt"}', 'https://neuoffenstadt.example.com/finanzausschuss',
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/organization/34',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

INSERT INTO public.oparl_resolved_organization (id, body_id, name, short_name, post, organization_type, classification,
                                                start_date, end_date, website, license, keyword, web, system_id, url,
                                                created, modified, deleted)
VALUES ('b6b1f18d-3147-4a2b-af0e-34f6204dc1d9', '903c26aa-dc8f-4d34-afd6-afdc5040dbd3',
        'Offenstädter Bürgerfraktion', 'OBF', '{}', 'Fraktion', 'Fraktion',
        '2025-11-01 00:00:00.000000', null, null, 'CC0-1.0', '{"fraktion"}', null,
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/organization/41',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

INSERT INTO public.oparl_resolved_meeting_organization
    (meeting_id, organization_id, organization_order)
VALUES ('1dd8b01e-a39c-4b40-9f24-46f7a42a3795', '9f7d37f9-cd93-4b11-a868-19a6c1e39318', 0);

-- OParl person
INSERT INTO public.oparl_resolved_person (id, body_id, name, family_name, given_name, form_of_address, affix, title,
                                          gender, phone, email, status, life, life_source, license, keyword, web,
                                          system_id, url, created, modified, deleted)
VALUES ('5e4cb965-6c76-4b31-a6ce-e8b26e68fb8b', '903c26aa-dc8f-4d34-afd6-afdc5040dbd3',
        'Prof. Dr. Max Mustermann', 'Mustermann', 'Max', 'Herr', 'MdL', '{"Prof.","Dr."}', 'male',
        '{"+493012345678"}', '{"max.mustermann@neuoffenstadt.example.com"}', '{"Bezirksbürgermeister"}',
        'Max Mustermann ist Mitglied des Stadtrats.', 'https://neuoffenstadt.example.com/rat/max-mustermann',
        'CC0-1.0', '{"stadtrat","finanzen"}', 'https://neuoffenstadt.example.com/person/max-mustermann',
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/person/29',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- OParl membership
INSERT INTO public.oparl_resolved_membership (id, body_id, person_id, organization_id, on_behalf_of_id, role,
                                              voting_right, start_date, end_date, license, keyword, web, system_id,
                                              url, created, modified, deleted)
VALUES ('47fdc541-cf5b-4e45-9bd4-46c8dcca9f34', '903c26aa-dc8f-4d34-afd6-afdc5040dbd3',
        '5e4cb965-6c76-4b31-a6ce-e8b26e68fb8b', '9f7d37f9-cd93-4b11-a868-19a6c1e39318',
        'b6b1f18d-3147-4a2b-af0e-34f6204dc1d9',
        'Vorsitz', true, '2025-11-01 00:00:00.000000', '2030-10-31 00:00:00.000000', 'CC0-1.0',
        '{"finanzausschuss","vorsitz"}', 'https://neuoffenstadt.example.com/mitgliedschaften/29-34',
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/membership/29-34',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- OParl consultation
INSERT INTO public.oparl_resolved_consultation (id, body_id, paper_id, agenda_item_id, meeting_id, authoritative, role,
                                                license, keyword, web, system_id, url, created, modified, deleted)
VALUES ('c92d284d-6899-49c2-9422-70ad7f4f38ea', '903c26aa-dc8f-4d34-afd6-afdc5040dbd3',
        'f6d47f4c-e5db-4dab-91f8-3bafc825d30c', '7b728a96-ae9a-40d1-b010-cc909ab2dd87',
        '1dd8b01e-a39c-4b40-9f24-46f7a42a3795', true, 'Beschlussfassung', 'CC0-1.0',
        '{"haushalt","entscheidung"}', 'https://neuoffenstadt.example.com/beratungen/47594',
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/consultation/47594',
        '2026-07-22 14:30:55.000000', '2026-07-24 13:05:27.000000', false);

INSERT INTO public.oparl_resolved_consultation_organization
    (consultation_id, organization_id, organization_order)
VALUES ('c92d284d-6899-49c2-9422-70ad7f4f38ea', '9f7d37f9-cd93-4b11-a868-19a6c1e39318', 0);

-- An unresolved consultation must not be exposed by the public API
INSERT INTO public.oparl_raw_consultation (id, paper_url, agenda_item_url, meeting_url, organization_urls,
                                           authoritative, role, license, keyword, web, system_id, url, created,
                                           modified, deleted)
VALUES ('d15aff89-2aa6-4f2d-966e-8797751ceda8',
        'https://oparl-demo.example.org/oparl/v1.1/paper/42',
        'https://oparl-demo.example.org/oparl/v1.1/agendaitem/3272',
        'https://oparl-demo.example.org/oparl/v1.1/meeting/281',
        '{"https://oparl-demo.example.org/oparl/v1.1/organization/34"}',
        false, 'Vorberatung', null, '{}', null,
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/consultation/999',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false);

-- Raw relation fixtures for the remaining resolver types
INSERT INTO public.oparl_raw_location
    (id, body_urls, description, keyword, created, modified, deleted, system_id, url)
VALUES ('10000000-0000-4000-8000-000000000005',
        '{"https://oparl-demo.example.org/oparl/v1.1/body/123"}', 'Noch nicht aufgelöster Ort', '{}',
        '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false,
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/location/unresolved');

INSERT INTO public.oparl_raw_membership
    (id, person_url, organization_url, on_behalf_of_url, role, keyword, created, modified, deleted, system_id, url)
VALUES ('10000000-0000-4000-8000-000000000006',
        'https://oparl-demo.example.org/oparl/v1.1/person/29',
        'https://oparl-demo.example.org/oparl/v1.1/organization/34',
        'https://oparl-demo.example.org/oparl/v1.1/organization/41',
        'Stellvertretung', '{}', '2026-07-22 14:30:55.000000', '2026-07-22 14:30:55.000000', false,
        '9352fa21-68c6-4f4e-8832-08a0454ff179',
        'https://oparl-demo.example.org/oparl/v1.1/membership/unresolved');
