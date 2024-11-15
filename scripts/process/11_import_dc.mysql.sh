#!/usr/bin/env bash
#
# Imports the issues to MySQL
#

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/scripts/set-mysql-vars.sh

php $ROOT/scripts/csv2sql.php ${OUTPUT_DIR}/dc.csv issue

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "SELECT COUNT(*) as 'issues before deleting DC' FROM issue"

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "DELETE FROM issue
WHERE recordId IN 
(SELECT recordId 
   FROM file_record AS fr 
   JOIN file AS f USING(file) 
   WHERE metadata_schema = 'DDB-DC');"

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "SELECT COUNT(*) AS 'issues after deleting DC' FROM issue"

echo "importing ${OUTPUT_DIR}/dc.sql into 'issue' table"
mysql $MYSQL_EXTRA_PARAMETERS $MY_DB < ${OUTPUT_DIR}/dc.sql

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "SELECT COUNT(*) AS 'issues after importing DC' FROM issue"
