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
java -cp $JAR de.gwdg.metadataqa.ddb.App \
  --schema src/main/resources/dc-schema.yaml \
  --input $INPUT_DIR/DC-DDB-WuerzburgTXT/UB_W-rzburg_Texte.xml \
  --output results/texts.csv \
  --format csv \
  --path solr/qa_ddb \
  --index

# analyse text
if [[ ! -d $OUTPUT_DIR/DC-DDB-WuerzburgTXT ]]; then
  mkdir $OUTPUT_DIR/DC-DDB-WuerzburgTXT
fi

java -cp $JAR de.gwdg.metadataqa.ddb.App \
  --schema src/main/resources/dc-schema.yaml \
  --input $INPUT_DIR/DC-DDB-WuerzburgTXT/UB_W-rzburg_Texte.xml \
  --output $OUTPUT_DIR/DC-DDB-WuerzburgTXT/raw.csv \
  --format csv \
  --path solr/qa_ddb

echo "DC-DDB-WuerzburgTXT/UB_W-rzburg_Texte.xml" > $OUTPUT_DIR/DC-DDB-WuerzburgTXT/filename

echo "Rscript scripts/process-texts.R"
# Rscript scripts/process-texts.R $OUTPUT_DIR/DC-DDB-WuerzburgTXT
Rscript scripts/process.R $OUTPUT_DIR/DC-DDB-WuerzburgTXT

# analyse images
if [[ ! -d $OUTPUT_DIR/DC-DDB-WuerzburgIMG ]]; then
  mkdir $OUTPUT_DIR/DC-DDB-WuerzburgIMG
fi

java -cp $JAR de.gwdg.metadataqa.ddb.App \
  --schema src/main/resources/dc-schema.yaml \
  --input $INPUT_DIR/DC-DDB-WuerzburgIMG/UB_W-rzburg_Bilder.xml \
  --output $OUTPUT_DIR/DC-DDB-WuerzburgIMG/raw.csv \
  --format csv

echo "DC-DDB-WuerzburgIMG/UB_W-rzburg_Bilder.xml" > $OUTPUT_DIR/DC-DDB-WuerzburgIMG/filename

echo "Rscript scripts/process-images.R"
Rscript scripts/process.R $OUTPUT_DIR/DC-DDB-WuerzburgIMG
