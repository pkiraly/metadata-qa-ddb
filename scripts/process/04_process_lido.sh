#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

java -Xmx4g -DlogDir="$ROOT/logs" -cp $ROOT/$JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --path solr/qa_ddb \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/LIDO \
  --schema $ROOT/src/main/resources/lido-schema.yaml \
  --output $OUTPUT_DIR/lido.csv \
  --record-address '//lido:lido'
