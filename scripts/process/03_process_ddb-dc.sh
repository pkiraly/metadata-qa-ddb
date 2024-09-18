#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

java -Xmx4g -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --solrHost ${SOLR_HOST} --solrPort ${SOLR_PORT} --path solr/qa_ddb_ddb_dc \
  --mysqlHost ${MY_HOST} --mysqlPort ${MY_PORT} --mysqlDatabase ${MY_DB} \
  --mysqlUser ${MY_USER} --mysqlPassword ${MY_PASSWORD} \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/DDB-DC \
  --schema $ROOT/src/main/resources/rdf-dc-schema.yaml \
  --oaiSchema $ROOT/src/main/resources/oai_dc-schema.yaml \
  --OAIPatterm "OAI_Harvest" \
  --output $OUTPUT_DIR/dc.csv \
  --record-address '//rdf:Description' \
  ${VALIDATION_PARAMS}


#  --record-address '//oai:record' \
