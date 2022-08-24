#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
echo $ROOT
source $ROOT/configuration.cnf

# extract files
find $INPUT_DIR/ -name "*.zip" \
  | while read filename; do 
      unzip -o -d "`dirname "$filename"`" "$filename";
      rm $filename;
    done;
