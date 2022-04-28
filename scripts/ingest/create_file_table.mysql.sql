DROP TABLE IF EXISTS file;
CREATE TABLE file (
  file TEXT,
  metadata_schema VARCHAR(20),
  provider_id VARCHAR(50),
  provider_name VARCHAR(200),
  set_id VARCHAR(50),
  set_name VARCHAR(200),
  datum VARCHAR(20),
  size INTEGER
);
CREATE INDEX f_file_idx ON file (file(500));
CREATE INDEX f_schema_idx ON file (metadata_schema);
CREATE INDEX f_set_id_idx ON file (set_id);
CREATE INDEX f_provider_id_idx ON file (provider_id);

