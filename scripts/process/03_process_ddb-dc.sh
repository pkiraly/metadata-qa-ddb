#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/solr-functions.sh

SOLR_CORE=${SOLR_CORE_PREFIX:-ddb_qa}_ddb_dc
# initialize $SOLR_CORE

java -Xmx4g -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --schemaName DDB-DC \
  --solrHost ${MQAF_SOLR_HOST} --solrPort ${MQAF_SOLR_PORT} --path solr/${SOLR_CORE} \
  --mysqlHost ${MQAF_DB_HOST} --mysqlPort ${MQAF_DB_PORT} --mysqlDatabase ${MQAF_DB_DATABASE} \
  --mysqlUser ${MQAF_DB_USER} --mysqlPassword ${MQAF_DB_PASSWORD} \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/DDB-DC \
  --schema $ROOT/schemas/rdf-dc-schema.yaml \
  --oaiSchema $ROOT/schemas/oai_dc-schema.yaml \
  --OAIPatterm "OAI_Harvest" \
  --output $OUTPUT_DIR/dc.csv \
  --record-address '//oai:record | //rdf:Description' \
  ${MQAF_VALIDATION_PARAMS}


#  --record-address '//oai:record' \
