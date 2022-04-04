#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/solr-functions.sh

MVN_REPO=/home/kiru/.m2/repository
CLASSPATH="$MVN_REPO/ch/qos/logback/logback-core/1.2.11/logback-core-1.2.11.jar"
CLASSPATH="$CLASSPATH:/$MVN_REPO/org/slf4j/slf4j-api/1.7.38/slf4j-api-1.7.38.jar"
CLASSPATH="$CLASSPATH:/$MVN_REPO/ch/qos/logback/logback-classic/1.2.11/logback-classic-1.2.11.jar"

SOLR_CORE=qa_ddb_marc
initialize $SOLR_CORE

java -Xmx4g -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --path solr/${SOLR_CORE} \
  --recursive \
  --indexing \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/MARC \
  --schema $ROOT/src/main/resources/marc-schema.yaml \
  --output $OUTPUT_DIR/marc.csv \
  --record-address '//marc:record'
