#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

php $ROOT/scripts/csv2sql.php ${OUTPUT_DIR}/mets-mods.csv issue

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "DELETE FROM issue
WHERE recordId IN 
(SELECT recordId 
   FROM file_record AS fr 
   JOIN file AS f USING(file) 
   WHERE metadata_schema = 'METS-MODS');"

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB < ${OUTPUT_DIR}/mets-mods.sql
