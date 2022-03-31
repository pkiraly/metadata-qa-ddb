#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

sqlite3 $OUTPUT_DIR/ddb.sqlite << EOT
.mode csv
DROP TABLE files;
.import $OUTPUT_DIR/files.csv files

CREATE INDEX IF NOT EXISTS "f_file_idx" ON "files" ("file");
CREATE INDEX IF NOT EXISTS "f_schema_idx" ON "files" ("schema");
CREATE INDEX IF NOT EXISTS "f_set_id_idx" ON "files" ("set_id");
CREATE INDEX IF NOT EXISTS "f_provider_id_idx" ON "files" ("provider_id");
EOT
