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
