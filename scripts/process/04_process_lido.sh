#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

java -Xmx4g -Djdk.xml.xpathExprOpLimit=200 -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --schemaName LIDO \
  --solrHost ${MQAF_SOLR_HOST} --solrPort ${MQAF_SOLR_PORT} --path solr/qa_ddb_lido \
  --mysqlHost ${MQAF_DB_HOST} --mysqlPort ${MQAF_DB_PORT} --mysqlDatabase ${MQAF_DB_DATABASE} \
  --mysqlUser ${MQAF_DB_USER} --mysqlPassword ${MQAF_DB_PASSWORD} \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/LIDO \
  --schema $ROOT/src/main/resources/lido-schema.yaml \
  --output $OUTPUT_DIR/lido.csv \
  --record-address '//lido:lido' \
  ${MQAF_VALIDATION_PARAMS}
