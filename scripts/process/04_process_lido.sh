#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/solr-functions.sh

SOLR_CORE=${MQAF_SOLR_CORE_PREFIX:-ddb_qa}_lido
# initialize $SOLR_CORE

java -Xmx4g -Djdk.xml.xpathExprOpLimit=200 -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --schemaName LIDO \
  --solrHost ${MQAF_SOLR_HOST} --solrPort ${MQAF_SOLR_PORT} --path solr/${SOLR_CORE} \
  --mysqlHost ${MQAF_DB_HOST} --mysqlPort ${MQAF_DB_PORT} --mysqlDatabase ${MQAF_DB_DATABASE} \
  --mysqlUser ${MQAF_DB_USER} --mysqlPassword ${MQAF_DB_PASSWORD} \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/LIDO \
  --schema $ROOT/schemas/lido-schema.yaml \
  --output $OUTPUT_DIR/lido.csv \
  --record-address '//lido:lido' \
  ${MQAF_VALIDATION_PARAMS}
