#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

php $ROOT/scripts/csv2sql.php ${OUTPUT_DIR}/edm-ddb.csv issue

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "SELECT COUNT(*) as 'before deleting DDB-EDM' FROM issue"

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "DELETE FROM issue 
WHERE recordId IN 
(SELECT recordId 
  FROM file_record AS fr 
  JOIN file AS f USING(file) 
  WHERE metadata_schema = 'DDB-EDM');"

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "SELECT COUNT(*) as 'after deleting DDB-EDM' FROM issue"

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB < ${OUTPUT_DIR}/edm-ddb.sql

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "SELECT COUNT(*) as 'after importing DDB-EDM' FROM issue"
