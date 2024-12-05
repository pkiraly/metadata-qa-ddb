#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/..)
source $ROOT/configuration.cnf
source $ROOT/scripts/set-mysql-vars.sh

# mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MQAF_DB_DATABASE < $ROOT/scripts/database.mysql.sql
echo "mysql $MYSQL_EXTRA_PARAMETERS $MQAF_DB_DATABASE < $ROOT/scripts/database.mysql.sql"

mysql $MYSQL_EXTRA_PARAMETERS $MQAF_DB_DATABASE < $ROOT/scripts/database.mysql.sql
