#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

java -Xmx4g -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --path solr/qa_ddb \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/MARC \
  --schema $ROOT/src/main/resources/marc-schema.yaml \
  --output $OUTPUT_DIR/marc.csv \
  --record-address '//marc:record'
