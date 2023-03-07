#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/scripts/set-mysql-vars.sh

php $ROOT/scripts/csv2sql.php $OUTPUT_DIR/files.csv file

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "DELETE FROM file;"

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB < $OUTPUT_DIR/files.sql
