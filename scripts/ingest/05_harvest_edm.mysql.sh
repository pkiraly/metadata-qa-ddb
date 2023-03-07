#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf
source $ROOT/scripts/set-mysql-vars.sh

mysql $MYSQL_EXTRA_PARAMETERS $MY_DB \
  -e 'SELECT set_id FROM file GROUP BY set_id ORDER BY COUNT(set_id) DESC;' \
 | sed 's/\t/,/g' \
  > $OUTPUT_DIR/datasets.csv

# rm files.csv

echo $OUTPUT_DIR
cat $OUTPUT_DIR/datasets.csv \
  | while read line; do
      echo $line
      php $(dirname $0)/oai-xml.php $INPUT_DIR/Europeana-EDM $line
    done
