#!/usr/bin/env bash
#
# Imports the issues to MySQL
#

ROOT=$(realpath $(dirname $0)/../..)
echo $ROOT
source $ROOT/configuration.cnf
source $ROOT/scripts/set-mysql-vars.sh

php $ROOT/scripts/csv2sql.php ${OUTPUT_DIR}/lido.csv issue

mysql $MYSQL_EXTRA_PARAMETERS $MQAF_DB_DATABASE -e "SELECT COUNT(*) as 'before deleting LIDO' FROM issue"

mysql $MYSQL_EXTRA_PARAMETERS $MQAF_DB_DATABASE -e "DELETE FROM issue
WHERE metadata_schema = 'LIDO' AND recordId IN
(SELECT recordId 
  FROM file_record AS fr 
  JOIN file AS f USING(file) 
  WHERE metadata_schema = 'LIDO');"

mysql $MYSQL_EXTRA_PARAMETERS $MQAF_DB_DATABASE -e "SELECT COUNT(*) AS 'after deleting LIDO' FROM issue"

mysql $MYSQL_EXTRA_PARAMETERS $MQAF_DB_DATABASE < ${OUTPUT_DIR}/lido.sql

mysql $MYSQL_EXTRA_PARAMETERS $MQAF_DB_DATABASE -e "SELECT COUNT(*) AS 'after importing LIDO' FROM issue"
