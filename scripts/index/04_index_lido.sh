#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/solr-functions.sh
source $ROOT/scripts/set-mysql-vars.sh

SOLR_CORE=qa_ddb_lido
initialize $SOLR_CORE

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB -e "DELETE FROM file_record WHERE file IN
(SELECT file FROM file WHERE metadata_schema = 'LIDO');"

java -Xmx4g -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --solrHost ${SOLR_HOST} --solrPort ${SOLR_PORT} --path solr/${SOLR_CORE} \
  --recursive \
  --indexing \
  --storing \
  --mysqlHost ${MY_HOST} --mysqlPort ${MY_PORT} --mysqlDatabase ${MY_DB} --mysqlUser ${MY_USER} --mysqlPassword ${MY_PASSWORD} \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/LIDO \
  --schema $ROOT/src/main/resources/lido-schema.yaml \
  --output $OUTPUT_DIR/lido.csv \
  --sqlitePath $OUTPUT_DIR/ddb-record.sqlite \
  --record-address '//lido:lido' \
  ${VALIDATION_PARAMS}
