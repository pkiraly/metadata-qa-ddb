#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/solr-functions.sh

MVN_REPO=/home/kiru/.m2/repository
CLASSPATH="$MVN_REPO/ch/qos/logback/logback-core/1.2.11/logback-core-1.2.11.jar"
CLASSPATH="$CLASSPATH:/$MVN_REPO/org/slf4j/slf4j-api/1.7.38/slf4j-api-1.7.38.jar"
CLASSPATH="$CLASSPATH:/$MVN_REPO/ch/qos/logback/logback-classic/1.2.11/logback-classic-1.2.11.jar"

SOLR_CORE=qa_ddb_ddb_edm
initialize $SOLR_CORE

mysql --defaults-extra-file=$ROOT/mysql-config.cnf $MY_DB -e "DELETE FROM file_record WHERE file IN 
(SELECT file FROM file WHERE metadata_schema = 'DDB-EDM');"

java -Xmx4g -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --path solr/${SOLR_CORE} \
  --recursive \
  --indexing \
  --storing \
  --mysqlDatabase $MY_DB --mysqlUser $MY_USER --mysqlPassword $MY_PASSWORD \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/DDB-EDM \
  --schema $ROOT/src/main/resources/edm-ddb-schema.yaml \
  --output $OUTPUT_DIR/edm-ddb.csv \
  --record-address '//rdf:RDF'

#  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
