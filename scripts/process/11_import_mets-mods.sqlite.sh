#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

php $ROOT/scripts/csv2sql.php ${OUTPUT_DIR}/mets-mods.csv issue
sqlite3 ${OUTPUT_DIR}/ddb.sqlite "DELETE FROM issue WHERE recordId IN 
(SELECT recordId FROM file_record AS fr JOIN files AS f USING(file) WHERE schema = 'METS-MODS');"
sqlite3 ${OUTPUT_DIR}/ddb.sqlite < ${OUTPUT_DIR}/mets-mods.sql
