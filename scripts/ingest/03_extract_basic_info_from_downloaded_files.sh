#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

if [[ ! -d $OUTPUT_DIR ]]; then
  mkdir $OUTPUT_DIR
fi
version=2
zipped=1

# unzip -l Q1_small_DC_BAW.zip | grep xml | sed -re 's/^.* [0-9]{2}:[0-9]{2} *//'
# export FILE=Q1_small_DC_BAW.zip && unzip -l $FILE | grep xml | sed -r "s/^.* [0-9]{2}:[0-9]{2} */$FILE,/"
# create database
echo "file,metadata_schema,provider_id,provider_name,set_id,set_name,datum,size" > $OUTPUT_DIR/files.csv
find $INPUT_DIR/ \
  | while read filename; do 
      if [[ -f $filename ]]; then
        echo "filename: ${filename}"
        datum=$(stat "${filename}" | grep Modify | sed -r 's,^.+: (.*)\..*$,\1,')
        size=$(stat "${filename}" | grep -oP 'Size: \K(\d+)')
        path=$(echo "${filename}" | sed "s,^$INPUT_DIR/,,")
        SCHEMA=$(echo $path | cut -d'/' -f1)
        # echo "$datum, $size, $path, $SCHEMA"
        LEVEL2=$(echo $path | cut -d'/' -f2)
        LEVEL3=$(echo $path | cut -d'/' -f3)
        if [[ "${version}" = "2" ]]; then
          PROV_ID=$LEVEL2
          HAS_MONA=$(echo $LEVEL2 | grep -o 'OAI_Harvest')
          if [[ $HAS_MONA ]]; then
            PROV_NAME=$(echo $LEVEL2 | sed 's,^.*_OAI_Harvest_,,' | sed 's,_, ,g')
          else
            PROV_NAME=$(echo $LEVEL2 | sed 's,^.*[_-]Q[0-9]_,,' | sed 's,_, ,g')
          fi
          SET_ID=""
          SET_NAME=""
        else
          PROV_ID=$(echo $LEVEL2 | cut -d'_' -f1)
          PROV_NAME=$(echo $LEVEL2 | cut -d'_' -f2)
          SET_ID=$(echo $LEVEL3 | cut -d'_' -f1)
          SET_NAME=$(echo $LEVEL3 | cut -d'_' -f2)
        fi
        if [[ $SCHEMA != "_ddb-output" ]]; then
          if [[ "${zipped}" = "1" ]]; then
            SUFFIX="$SCHEMA,$PROV_ID,$PROV_NAME,$SET_ID,$SET_NAME,$datum,$size"
            unzip -l $filename \
              | grep xml \
              | sed -r "s/^.* [0-9]{2}:[0-9]{2} *//" \
              | sed -r "s/Ф/ö/" \
              | sed -r "s/Д/ä/" \
              | sed -r "s/Б/ü/" \
              | sed "s,^,$path::," \
              | sed "s|$|,$SUFFIX|" \
              >> $OUTPUT_DIR/files.csv
          else
            echo "$path,$SCHEMA,$PROV_ID,$PROV_NAME,$SET_ID,$SET_NAME,$datum,$size" >> $OUTPUT_DIR/files.csv
          fi
        fi
      fi
    done
