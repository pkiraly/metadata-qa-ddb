#!/usr/bin/env bash

source $(dirname $0)/../../configuration.cnf

if [[ ! -d $INPUT_DIR ]]; then
  mkdir $INPUT_DIR 
fi
if [[ ! -d $OUTPUT_DIR ]]; then
  mkdir $OUTPUT_DIR 
fi

# download files
# wget -r \
#      --directory-prefix $INPUT_DIR \
#      --no-host-directories \
#      --no-parent \
#      --cut-dirs=1 \
#      ftp://$FTP_USER:$FTP_PW@ftp.deutsche-digitale-bibliothek.de/MQ-Feedback-Tool/

# extract files
# find $INPUT_DIR -name "*.zip" \
#   | while read filename; do 
#       unzip -o -d "`dirname "$filename"`" "$filename";
#       rm $filename;
#     done;

# create database
# echo "file,schema,provider_id,provider_name,set_id,set_name,datum,size" > $OUTPUT_DIR/files.csv
# find $INPUT_DIR \
#   | while read filename; do 
#       if [[ -f $filename ]]; then
#         datum=$(stat $filename | grep Modify | sed -r 's,^.+: (.*)\..*$,\1,')
#         size=$(stat $filename | grep -oP 'Size: \K(\d+)')
#         path=$(echo $filename | sed "s,^$INPUT_DIR/,,")
#         SCHEMA=$(echo $path | cut -d'/' -f1)
#         LEVEL2=$(echo $path | cut -d'/' -f2)
#         LEVEL3=$(echo $path | cut -d'/' -f3)
#         PROV_ID=$(echo $LEVEL2 | cut -d'_' -f1)
#         PROV_NAME=$(echo $LEVEL2 | cut -d'_' -f2)
#         SET_ID=$(echo $LEVEL3 | cut -d'_' -f1)
#         SET_NAME=$(echo $LEVEL3 | cut -d'_' -f2)
#         echo "$path,$SCHEMA,$PROV_ID,$PROV_NAME,$SET_ID,$SET_NAME,$datum,$size" >> $OUTPUT_DIR/files.csv
#       fi
#     done

# sqlite3 $OUTPUT_DIR/ddb.sqlite << EOT
# .mode csv
# DROP TABLE files;
# .import $OUTPUT_DIR/files.csv files
# EOT

sqlite3 $OUTPUT_DIR/ddb.sqlite 'SELECT set_id FROM files GROUP BY set_id ORDER BY COUNT(set_id) DESC;' \
  > $OUTPUT_DIR/datasets.csv

# rm files.csv

echo $OUTPUT_DIR
cat $OUTPUT_DIR/datasets.csv | while read line; do
  echo $line
  php oai-xml.php $INPUT_DIR/_ddb-output $line
done