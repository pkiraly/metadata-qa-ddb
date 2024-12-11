#!/usr/bin/env bash

ROOT=$(realpath $(dirname $0))
source $ROOT/configuration.cnf
echo "OUTPUT_DIR: $OUTPUT_DIR"

PROCESS_DC=1
PROCESS_LIDO=1

rm $OUTPUT_DIR/*

# create empty tables
scripts/create_database.mysql.sh
# creates files.csv
scripts/ingest/03_extract_basic_info_from_downloaded_files.sh
# import into files table
scripts/ingest/04_import_basic_info.mysql.sh

if [[ "${PROCESS_DC}" == "1" ]]; then
  # populate file_record in MySQL,
  # create and populate record in SQLite3
  # index record with Solr
  scripts/index/03_index_ddb-dc.sh
  # create issue.csv
  scripts/process/03_process_ddb-dc.sh
  # import into issue table
  scripts/process/11_import_dc.mysql.sh
fi

if [[ "${PROCESS_LIDO}" == "1" ]]; then
  # populate file_record, and record in SQLite3
  scripts/index/04_index_lido.sh
  # create issue.csv
  scripts/process/04_process_lido.sh
  # import into issue table
  scripts/process/11_import_lido.mysql.sh
fi

scripts/process/12_calculate_aggregations.mysql.sh
