#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

sqlite3 ${OUTPUT_DIR}/ddb.sqlite  << EOF
.mode csv
.headers 'on'
.output ${OUTPUT_DIR}/all-issues.csv
SELECT f.schema, f.set_id, f.provider_id, f.file, i.*
  FROM issue AS i
  LEFT JOIN file_record AS r ON (r.recordId = i.recordId)
  LEFT JOIN files AS f USING(file);
EOF

Rscript $ROOT/scripts/R/process-all.R ${OUTPUT_DIR}

sqlite3 ${OUTPUT_DIR}/ddb.sqlite  << EOF
.mode csv
DROP TABLE IF EXISTS variability;
.import ${OUTPUT_DIR}/variability.csv variability
DROP TABLE IF EXISTS frequency;
.import ${OUTPUT_DIR}/frequency.csv frequency
DROP TABLE IF EXISTS count;
.import ${OUTPUT_DIR}/count.csv count
EOF
