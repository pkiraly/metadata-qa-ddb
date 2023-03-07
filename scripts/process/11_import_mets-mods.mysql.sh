#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/scripts/set-mysql-vars.sh

php $ROOT/scripts/csv2sql.php ${OUTPUT_DIR}/mets-mods.csv issue

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "SELECT COUNT(*) as 'before deleting METS-MODS' FROM issue"

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "DELETE FROM issue
WHERE recordId IN 
(SELECT recordId 
   FROM file_record AS fr 
   JOIN file AS f USING(file) 
   WHERE metadata_schema = 'METS-MODS');"

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "SELECT COUNT(*) as 'after deleting METS-MODS' FROM issue"

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB < ${OUTPUT_DIR}/mets-mods.sql

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "SELECT COUNT(*) as 'after importing METS-MODS' FROM issue"
