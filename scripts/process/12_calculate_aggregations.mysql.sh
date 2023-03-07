#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/scripts/set-mysql-vars.sh

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB  -e "SELECT f.metadata_schema, f.set_id, f.provider_id, f.file, i.*
  FROM issue AS i
  LEFT JOIN file_record AS r ON (r.recordId = i.recordId)
  LEFT JOIN file AS f USING(file);" | sed 's/\t/,/g' > ${OUTPUT_DIR}/all-issues.csv

Rscript $ROOT/scripts/R/process-all.R ${OUTPUT_DIR}

echo variability
mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "DELETE FROM variability;"
php $ROOT/scripts/csv2sql.php ${OUTPUT_DIR}/variability.csv variability
mysql $MYSQL_EXTRA_PARAMETERS $MY_DB < ${OUTPUT_DIR}/variability.sql

echo frequency
mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "DELETE FROM frequency;"
php $ROOT/scripts/csv2sql.php ${OUTPUT_DIR}/frequency.csv frequency value
mysql $MYSQL_EXTRA_PARAMETERS $MY_DB < ${OUTPUT_DIR}/frequency.sql

echo count
mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "DELETE FROM count;"
php $ROOT/scripts/csv2sql.php ${OUTPUT_DIR}/count.csv count
mysql $MYSQL_EXTRA_PARAMETERS $MY_DB < ${OUTPUT_DIR}/count.sql
