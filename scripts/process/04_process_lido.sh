#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

java -Xmx4g -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --solrHost ${SOLR_HOST} --solrPort ${SOLR_PORT} --path solr/qa_ddb_lido \
  --mysqlHost ${MY_HOST} --mysqlPort ${MY_PORT} --mysqlDatabase ${MY_DB} --mysqlUser ${MY_USER} --mysqlPassword ${MY_PASSWORD} \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/LIDO \
  --schema $ROOT/src/main/resources/lido-schema.yaml \
  --output $OUTPUT_DIR/lido.csv \
  --record-address '//lido:lido' \
  ${VALIDATION_PARAMS}
