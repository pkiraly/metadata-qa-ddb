#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/solr-functions.sh
source $ROOT/scripts/set-mysql-vars.sh

SOLR_CORE=${MQAF_SOLR_CORE_PREFIX:-qa_ddb}_lido
initialize $SOLR_CORE

mysql $MYSQL_EXTRA_PARAMETERS $MQAF_DB_DATABASE -e "DELETE FROM file_record WHERE file IN
(SELECT file FROM file WHERE metadata_schema = 'LIDO');"

java -Xmx4g -Djdk.xml.xpathExprOpLimit=200 -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --schemaName LIDO \
  --solrHost ${MQAF_SOLR_HOST} --solrPort ${MQAF_SOLR_PORT} --path solr/${MQAF_SOLR_CORE} \
  --recursive \
  --indexing \
  --storing \
  --mysqlHost ${MQAF_DB_HOST} --mysqlPort ${MQAF_DB_PORT} --mysqlDatabase ${MQAF_DB_DATABASE} \
  --mysqlUser ${MQAF_DB_USER} --mysqlPassword ${MQAF_DB_PASSWORD} \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/LIDO \
  --schema $ROOT/schemas/lido-schema.yaml \
  --output $OUTPUT_DIR/lido.csv \
  --sqlitePath $OUTPUT_DIR/ddb-record.sqlite \
  --record-address '//lido:lido' \
  ${MQAF_VALIDATION_PARAMS}
