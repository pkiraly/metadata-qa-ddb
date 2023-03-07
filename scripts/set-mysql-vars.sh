#!/usr/bin/env bash

if [[ "$MYSQL_CONFIG_TYPE" == "" ]]; then
  MYSQL_CONFIG_TYPE=LOCAL_FILE
fi

echo "MYSQL_CONFIG_TYPE: $MYSQL_CONFIG_TYPE"
MYSQL_CONFIG_FILE=
if [[ "$MYSQL_CONFIG_TYPE" == "LOCAL_FILE" ]]; then
  MYSQL_CONFIG_FILE=$ROOT/mysql-config.cnf
elif [ "$MYSQL_CONFIG_TYPE" == "TEMP_FILE" ]; then
  MYSQL_CONFIG_FILE=/tmp/ddb-mysql-config.cnf
fi

echo "MYSQL_CONFIG_FILE: $MYSQL_CONFIG_FILE"

MYSQL_EXTRA_PARAMETERS=
if [[ "$MYSQL_CONFIG_FILE" != "" ]]; then
  if [[ ! -f $MYSQL_CONFIG_FILE ]]; then
    echo "[client]"                     > $MYSQL_CONFIG_FILE
    echo "user = \"$MY_USER\""         >> $MYSQL_CONFIG_FILE
    echo "password = \"$MY_PASSWORD\"" >> $MYSQL_CONFIG_FILE
    echo "host = \"$MY_HOST\""         >> $MYSQL_CONFIG_FILE
    echo "port = \"$MY_PORT\""         >> $MYSQL_CONFIG_FILE
  fi
  MYSQL_EXTRA_PARAMETERS="--defaults-extra-file=$MYSQL_CONFIG_FILE"
elif [[ "$MYSQL_CONFIG_TYPE" == "ENVIRONMENT_VARIABLES" ]]; then
  export MYSQL_PWD=$MY_PASSWORD
  export MYSQL_HOST=$MY_HOST
  export MYSQL_TCP_PORT=$MY_PORT
  MYSQL_EXTRA_PARAMETERS="-u $MY_USER"
else
  echo "Set MYSQL_CONFIG_TYPE in the configuration!"
  exit
fi
