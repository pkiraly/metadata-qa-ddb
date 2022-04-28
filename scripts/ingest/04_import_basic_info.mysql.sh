#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

# mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB < $ROOT/scripts/ingest/create_file_table.mysql.sql

php $ROOT/scripts/csv2sql.php $OUTPUT_DIR/files.csv file

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB < $OUTPUT_DIR/files.sql
