-- OParl system
INSERT INTO oparl_system (id, url)
VALUES ('9352fa21-68c6-4f4e-8832-08a0454ff179', 'https://oparl-demo.example.org');

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
