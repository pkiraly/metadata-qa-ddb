#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/solr-functions.sh

SOLR_CORE=${SOLR_CORE_PREFIX:-qa_ddb}_ddb_edm
initialize $SOLR_CORE

java -Xmx4g -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --solrHost ${MQAF_SOLR_HOST} --solrPort ${MQAF_SOLR_PORT} --path solr/${SOLR_CORE} \
  --mysqlHost ${MQAF_DB_HOST} --mysqlPort ${MQAF_DB_PORT} --mysqlDatabase ${MQAF_DB_DATABASE} \
  --mysqlUser ${MQAF_DB_USER} --mysqlPassword ${MQAF_DB_PASSWORD} \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/DDB-EDM \
  --schema $ROOT/schemas/ddb-edm-schema.yaml \
  --output $OUTPUT_DIR/edm-ddb.csv \
  --record-address '//rdf:RDF' \
  ${MQAF_VALIDATION_PARAMS}
