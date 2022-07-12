#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

php $ROOT/scripts/csv2sql.php ${OUTPUT_DIR}/marc.csv issue

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "SELECT COUNT(*) as 'before deleting MARC' FROM issue"

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "DELETE FROM issue 
WHERE recordId IN 
(SELECT recordId 
  FROM file_record AS fr 
  JOIN file AS f USING(file) 
  WHERE metadata_schema = 'MARC');"

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "SELECT COUNT(*) as 'after deleting MARC' FROM issue"

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB < ${OUTPUT_DIR}/marc.sql

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "SELECT COUNT(*) as 'after importing MARC' FROM issue"
