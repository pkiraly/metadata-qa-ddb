#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/..)
source $ROOT/configuration.cnf

if [[ ! -f $ROOT/mysql-config.cnf ]]; then
  echo "[client]"                     > $ROOT/mysql-config.cnf
  echo "user = \"$MY_USER\""         >> $ROOT/mysql-config.cnf
  echo "password = \"$MY_PASSWORD\"" >> $ROOT/mysql-config.cnf
  echo "host = \"$MY_HOST\""         >> $ROOT/mysql-config.cnf
  echo "port = \"$MY_PORT\""         >> $ROOT/mysql-config.cnf
fi

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB < $ROOT/scripts/database.mysql.sql
