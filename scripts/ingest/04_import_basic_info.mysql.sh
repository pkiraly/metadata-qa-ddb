#!/usr/bin/env bash
# - - - -
# Imports file list into MySQL
# - - - -

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/scripts/set-mysql-vars.sh

php $ROOT/scripts/csv2sql.php $OUTPUT_DIR/files.csv file

mysql $MYSQL_EXTRA_PARAMETERS $MQAF_DB_DATABASE -e "DELETE FROM file;"

mysql $MYSQL_EXTRA_PARAMETERS $MQAF_DB_DATABASE < $OUTPUT_DIR/files.sql
