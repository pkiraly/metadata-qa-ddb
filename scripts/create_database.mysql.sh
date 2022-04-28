#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/..)
source $ROOT/configuration.cnf

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB < $ROOT/scripts/database.mysql.sql

