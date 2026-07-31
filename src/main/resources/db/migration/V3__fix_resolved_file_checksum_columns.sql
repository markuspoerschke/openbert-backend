ALTER TABLE oparl_resolved_file
    RENAME COLUMN sha1checksum TO sha1_checksum;

ALTER TABLE oparl_resolved_file
    RENAME COLUMN sha512checksum TO sha512_checksum;
