#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

php $ROOT/scripts/csv2sql.php ${OUTPUT_DIR}/lido.csv issue

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "SELECT COUNT(*) as 'before deleting LIDO' FROM issue"

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "DELETE FROM issue 
WHERE recordId IN 
(SELECT recordId 
  FROM file_record AS fr 
  JOIN file AS f USING(file) 
  WHERE metadata_schema = 'LIDO');"

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "SELECT COUNT(*) AS 'after deleting LIDO' FROM issue"

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB < ${OUTPUT_DIR}/lido.sql

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "SELECT COUNT(*) AS 'after importing LIDO' FROM issue"
