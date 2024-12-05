#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/solr-functions.sh
source $ROOT/scripts/set-mysql-vars.sh

SOLR_CORE=${SOLR_CORE_PREFIX:-qa_ddb}_mets_mods
initialize $SOLR_CORE

mysql $MYSQL_EXTRA_PARAMETERS $MQAF_DB_DATABASE -e "DELETE FROM file_record WHERE file IN
(SELECT file FROM file WHERE metadata_schema = 'METS-MODS');"

java -Xmx4g -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --solrHost ${MQAF_SOLR_HOST} --solrPort ${MQAF_SOLR_PORT} --path solr/${SOLR_CORE} \
  --recursive \
  --indexing \
  --storing \
  --mysqlHost ${MQAF_DB_HOST} --mysqlPort ${MQAF_DB_PORT} --mysqlDatabase ${MQAF_DB_DATABASE} \
  --mysqlUser ${MQAF_DB_USER} --mysqlPassword ${MQAF_DB_PASSWORD} \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/METS-MODS \
  --schema $ROOT/schemas/mets-mods-schema.yaml \
  --output $OUTPUT_DIR/mets-mods.csv \
  --sqlitePath $OUTPUT_DIR/ddb-record.sqlite \
  --record-address '//mets:mets' \
  ${MQAF_VALIDATION_PARAMS}
