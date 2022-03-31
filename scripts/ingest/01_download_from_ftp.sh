#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0)/../..)
source $ROOT/configuration.cnf

if [[ ! -d $INPUT_DIR ]]; then
  mkdir $INPUT_DIR
fi
if [[ ! -d $OUTPUT_DIR ]]; then
  mkdir $OUTPUT_DIR
fi

# download files
wget -r \
     --directory-prefix $INPUT_DIR \
     --no-host-directories \
     --no-parent \
     --cut-dirs=1 \
     ftp://$FTP_USER:$FTP_PW@ftp.deutsche-digitale-bibliothek.de/MQ-Feedback-Tool/
