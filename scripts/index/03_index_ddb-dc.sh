#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/solr-functions.sh
source $ROOT/scripts/set-mysql-vars.sh

MVN_REPO=~/.m2/repository
CLASSPATH="$MVN_REPO/ch/qos/logback/logback-core/1.2.11/logback-core-1.2.11.jar"
CLASSPATH="$CLASSPATH:/$MVN_REPO/org/slf4j/slf4j-api/1.7.38/slf4j-api-1.7.38.jar"
CLASSPATH="$CLASSPATH:/$MVN_REPO/ch/qos/logback/logback-classic/1.2.11/logback-classic-1.2.11.jar"
# java -Xmx4g -DlogDir="$ROOT/logs" -cp $CLASSPATH:$ROOT/$JAR de.gwdg.metadataqa.ddb.App \

SOLR_CORE=qa_ddb_ddb_dc
initialize $SOLR_CORE

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "DELETE FROM file_record WHERE file IN
(SELECT file FROM file WHERE metadata_schema = 'DDB-DC');"

java -Xmx4g -DlogDir="$ROOT/logs" -cp $CLASSPATH:$ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --solrHost ${SOLR_HOST} --solrPort ${SOLR_PORT} --path solr/${SOLR_CORE} \
  --recursive \
  --indexing \
  --storing \
  --mysqlHost ${MY_HOST} --mysqlPort ${MY_PORT} --mysqlDatabase ${MY_DB} --mysqlUser ${MY_USER} --mysqlPassword ${MY_PASSWORD} \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/DDB-DC \
  --schema $ROOT/src/main/resources/dc-schema.yaml \
  --output $OUTPUT_DIR/dc.csv \
  --sqlitePath $OUTPUT_DIR/ddb-record.sqlite \
  --record-address '//oai:record'
