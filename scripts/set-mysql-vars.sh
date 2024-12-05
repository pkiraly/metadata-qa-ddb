#!/usr/bin/env bash
# - - - -
# Imports the content of a config file to a mysql specific config file or to environment variables
# input environmental variable:
# - MYSQL_CONFIG_TYPE: 'LOCAL_FILE', 'TEMP_FILE', 'ENVIRONMENT_VARIABLES'
# - - - -

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
    echo "user = \"$MQAF_DB_USER\""         >> $MYSQL_CONFIG_FILE
    echo "password = \"$MQAF_DB_PASSWORD\"" >> $MYSQL_CONFIG_FILE
    echo "host = \"$MQAF_DB_HOST\""         >> $MYSQL_CONFIG_FILE
    echo "port = \"$MQAF_DB_PORT\""         >> $MYSQL_CONFIG_FILE
  fi
  MYSQL_EXTRA_PARAMETERS="--defaults-extra-file=$MYSQL_CONFIG_FILE"
elif [[ "$MYSQL_CONFIG_TYPE" == "ENVIRONMENT_VARIABLES" ]]; then
  export MYSQL_PWD=$MQAF_DB_PASSWORD
  export MYSQL_HOST=$MQAF_DB_HOST
  export MYSQL_TCP_PORT=$MQAF_DB_PORT
  MYSQL_EXTRA_PARAMETERS="-u $MQAF_DB_USER"
else
  echo "Set MYSQL_CONFIG_TYPE in the configuration!"
  exit
fi
