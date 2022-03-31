#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

sqlite3 $OUTPUT_DIR/ddb.sqlite 'SELECT set_id FROM files GROUP BY set_id ORDER BY COUNT(set_id) DESC;' \
  > $OUTPUT_DIR/datasets.csv

# rm files.csv

echo $OUTPUT_DIR
cat $OUTPUT_DIR/datasets.csv \
  | while read line; do
      echo $line
      php $(dirname $0)/oai-xml.php $INPUT_DIR/_ddb-output $line
    done
