#!/usr/bin/env bash

source configuration.cnf
SOLR_HOST=${SOLR_HOST:-http://localhost:${PORT:-8983}}

check_core() {
  LOCAL_CORE=$1
  LOCAL_URL=$(printf "%s/solr/admin/cores?action=STATUS&core=%s" $SOLR_HOST $LOCAL_CORE)
  CORE_EXISTS=$(curl -s "$LOCAL_URL" | jq .status | grep "\"$LOCAL_CORE\":" | grep -c -P '{$')
  # use echo instead of return
  echo $CORE_EXISTS
}

create_core() {
  LOCAL_CORE=$1
  echo "creating Solr index: ${LOCAL_CORE} at $SOLR_HOST"
  curl -s "$SOLR_HOST/solr/admin/cores?action=CREATE&name=$LOCAL_CORE&configSet=_default"
}

CORE=qa_ddb
PROD_EXISTS=$(check_core $CORE)
echo "$CORE exists: $PROD_EXISTS"
if [[ $PROD_EXISTS != 1 ]]; then
  echo "Create Solr core '$CORE'"
  create_core $CORE
fi

curl $SOLR_HOST/solr/qa_ddb/update -H "Content-type: text/xml" --data-binary '<delete><query>*:*</query></delete>'
curl "$SOLR_HOST/solr/qa_ddb/update?optimize=true" -H 'Content-type: text/xml' --data-binary '<commit/>'

JAR=target/metadata-qa-ddb-1.0-SNAPSHOT-jar-with-dependencies.jar

# index text
# TODO: disable deletion
java -cp $JAR de.gwdg.metadataqa.ddb.App \
  --schema src/main/resources/dc-schema.yaml \
  --input $INPUT_DIR/DC-DDB-WuerzburgTXT/UB_W-rzburg_Texte.xml \
  --output results/texts.csv \
  --format csv \
  --path solr/qa_ddb \
  --index

# analyse text
DIR=${OUTPUT_DIR}/DC-DDB-WuerzburgTXT
if [[ ! -d ${DIR} ]]; then
  mkdir ${DIR}
fi

if [[ -f ${DIR}/qa.sqlite ]]; then
  rm ${DIR}/qa.sqlite
fi

java -cp $JAR de.gwdg.metadataqa.ddb.App \
  --schema src/main/resources/dc-schema.yaml \
  --input $INPUT_DIR/DC-DDB-WuerzburgTXT/UB_W-rzburg_Texte.xml \
  --output ${DIR}/raw.csv \
  --format csv \
  --path solr/qa_ddb \
  --sqlitePath ${DIR}/qa.sqlite

echo "DC-DDB-WuerzburgTXT/UB_W-rzburg_Texte.xml" > ${DIR}/filename

echo ${DIR}/raw.csv

echo "Rscript scripts/process-texts.R"
Rscript scripts/process.R ${DIR}

sqlite3 ${DIR}/qa.sqlite << EOF
.mode csv
.import ${DIR}/raw.csv issue
EOF

# analyse images
DIR=${OUTPUT_DIR}/DC-DDB-WuerzburgIMG
if [[ ! -d ${DIR} ]]; then
  mkdir ${DIR}
fi

if [[ -f ${DIR}/qa.sqlite ]]; then
  rm ${DIR}/qa.sqlite
fi

# index text
java -cp $JAR de.gwdg.metadataqa.ddb.App \
  --schema src/main/resources/dc-schema.yaml \
  --input $INPUT_DIR/DC-DDB-WuerzburgIMG/UB_W-rzburg_Bilder.xml \
  --output ${DIR}/raw.csv \
  --format csv \
  --path solr/qa_ddb \
  --index

java -cp $JAR de.gwdg.metadataqa.ddb.App \
  --schema src/main/resources/dc-schema.yaml \
  --input $INPUT_DIR/DC-DDB-WuerzburgIMG/UB_W-rzburg_Bilder.xml \
  --output ${DIR}/raw.csv \
  --format csv \
  --sqlitePath ${DIR}/qa.sqlite


echo "DC-DDB-WuerzburgIMG/UB_W-rzburg_Bilder.xml" > ${DIR}/filename

echo "Rscript scripts/process-images.R"
Rscript scripts/process.R ${DIR}

sqlite3 ${DIR}/qa.sqlite << EOF
.mode csv
.import ${DIR}/raw.csv issue
EOF

java -cp $JAR de.gwdg.metadataqa.ddb.App \
  --schema src/main/resources/marc-schema.yaml \
  --directory /home/kiru/temp/source/MARC_BSB/oai_bsb_84 \
  --output /home/kiru/temp/raw.csv \
  --format csv \
  --path solr/qa_ddb \
  --sqlitePath /home/kiru/temp/qa.sqlite \
  --record-address '//marc:record' \
  --recursive

exit

EDM_Bamberg
  EDM-OAI: split: //record - metadata/rdf:RDF

DDB-EDM_BSB2-0
  EDM

EDM-DDB-Bamberg-0
  EDM

EDM-DDB-WuerzburgIMG-0
  EDM

EDM-DDB-WuerzburgTXT-0
  EDM

LIDO_dmm_digiporta
  LIDO: split: //lido:lido

LIDO_IfL
  LIDO

LIDO_sddm
  LIDO: split: //lido:lido

LIDO_UB-HB_anaill
  LIDO: split: //record //lido:lido

MARC_BSB
  MARC <mx:record xmlns:mx="info:lc/xmlns/marcxchange-v1"

METSMODS_2021
  METSMODS split: //record //mets:mets

