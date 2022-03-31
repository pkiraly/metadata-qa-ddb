#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

sqlite3 ${OUTPUT_DIR}/ddb.sqlite << EOF
DROP TABLE IF EXISTS file_record;
CREATE TABLE file_record AS SELECT file, id AS recordId FROM record;
CREATE INDEX "fr_file_idx" ON "file_record" ("file");
CREATE INDEX "fr_recordId_idx" ON "file_record" ("recordId");
EOF