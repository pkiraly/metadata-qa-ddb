#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/scripts/set-mysql-vars.sh

mysql $MYSQL_EXTRA_PARAMETERS $MQAF_DB_DATABASE < $ROOT/scripts/process/create_issue_table.mysql.sql

