#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

# create database
echo "file,metadata_schema,provider_id,provider_name,set_id,set_name,datum,size" > $OUTPUT_DIR/files.csv
find $INPUT_DIR/ \
  | while read filename; do 
      if [[ -f $filename ]]; then
        datum=$(stat $filename | grep Modify | sed -r 's,^.+: (.*)\..*$,\1,')
        size=$(stat $filename | grep -oP 'Size: \K(\d+)')
        path=$(echo $filename | sed "s,^$INPUT_DIR/,,")
        SCHEMA=$(echo $path | cut -d'/' -f1)
        # echo "$datum, $size, $path, $SCHEMA"
        LEVEL2=$(echo $path | cut -d'/' -f2)
        LEVEL3=$(echo $path | cut -d'/' -f3)
        PROV_ID=$(echo $LEVEL2 | cut -d'_' -f1)
        PROV_NAME=$(echo $LEVEL2 | cut -d'_' -f2)
        SET_ID=$(echo $LEVEL3 | cut -d'_' -f1)
        SET_NAME=$(echo $LEVEL3 | cut -d'_' -f2)
        if [[ $SCHEMA != "_ddb-output" ]]; then
          echo "$path,$SCHEMA,$PROV_ID,$PROV_NAME,$SET_ID,$SET_NAME,$datum,$size" >> $OUTPUT_DIR/files.csv
        fi
      fi
    done
