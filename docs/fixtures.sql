INSERT INTO oparl_system (id, url)
VALUES ('f55716d3-6721-46e8-93e2-dcdcc58a9cad', 'https://ris.krefeld.de/webservice/oparl/v1.1/system');

INSERT INTO crawl_target (id, system_id, url, created_at, last_crawled_at, modified_at, reference_count,
                          scheduled_at, job_id)
VALUES ('54e52151-c774-445c-bc39-30964f125592', 'f55716d3-6721-46e8-93e2-dcdcc58a9cad',
        'https://ris.krefeld.de/webservice/oparl/v1.1/body/1', '2026-07-29 16:07:33.000000', null,
        '2026-07-29 16:07:36.000000', 1, null, null);
