#!/usr/bin/env bash

#  --sqlitePath /home/kiru/temp/qa.sqlite \

source configuration.cnf

JAR=target/metadata-qa-ddb-1.0-SNAPSHOT-jar-with-dependencies.jar

java -Xmx4g -cp $JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --path solr/qa_ddb \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/DDB-EDM \
  --schema schemas/ddb-edm-schema.yaml \
  --output $OUTPUT_DIR/edm-ddb.csv \
  --record-address '//rdf:RDF'

java -Xmx4g -cp $JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --path solr/qa_ddb \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/MARC \
  --schema schemas/marc-schema.yaml \
  --output $OUTPUT_DIR/marc.csv \
  --record-address '//marc:record'

java -Xmx4g -cp $JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --path solr/qa_ddb \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/DDB-DC \
  --schema schemas/dc-schema.yaml \
  --output $OUTPUT_DIR/dc.csv \
  --record-address '//oai:record'

java -Xmx4g -cp $JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --path solr/qa_ddb \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/LIDO \
  --schema schemas/lido-schema.yaml \
  --output $OUTPUT_DIR/lido.csv \
  --record-address '//lido:lido'

java -Xmx4g -cp $JAR de.gwdg.metadataqa.ddb.App \
  --format csv \
  --path solr/qa_ddb \
  --recursive \
  --sqlitePath $OUTPUT_DIR/ddb.sqlite \
  --rootDirectory $INPUT_DIR \
  --directory $INPUT_DIR/METS-MODS \
  --schema schemas/mets-mods-schema.yaml \
  --output $OUTPUT_DIR/mets-mods.csv \
  --record-address '//mets:mets'

sqlite3 ${OUTPUT_DIR}/ddb.sqlite << EOF
DROP TABLE IF EXISTS file_record;
CREATE TABLE file_record AS SELECT file, id AS recordId FROM record;
CREATE INDEX "fr_file_idx" ON "file_record" ("file");
CREATE INDEX "fr_recordId_idx" ON "file_record" ("recordId");

DROP TABLE "issue";
CREATE TABLE "issue"(
  "recordId" TEXT,
  "providerid" TEXT,
  "Q-1.1:status" TEXT,
  "Q-1.1:score" INTEGER,
  "Q-1.5:status" TEXT,
  "Q-1.5:score" INTEGER,
  "Q-2.1:status" TEXT,
  "Q-2.1:score" INTEGER,
  "Q-2.2:status" TEXT,
  "Q-2.2:score" INTEGER,
  "Q-2.4:status" TEXT,
  "Q-2.4:score" INTEGER,
  "Q-2.5:status" TEXT,
  "Q-2.5:score" INTEGER,
  "Q-2.6:status" TEXT,
  "Q-2.6:score" INTEGER,
  "Q-3.1:status" TEXT,
  "Q-3.1:score" INTEGER,
  "Q-3.2:status" TEXT,
  "Q-3.2:score" INTEGER,
  "Q-3.3:status" TEXT,
  "Q-3.3:score" INTEGER,
  "Q-3.5:status" TEXT,
  "Q-3.5:score" INTEGER,
  "Q-6.1:status" TEXT,
  "Q-6.1:score" INTEGER,
  "Q-6.3:status" TEXT,
  "Q-6.3:score" INTEGER,
  "Q-6.5:status" TEXT,
  "Q-6.5:score" INTEGER,
  "Q-7.1:status" TEXT,
  "Q-7.1:score" INTEGER,
  "Q-7.3:status" TEXT,
  "Q-7.3:score" INTEGER,
  "Q-7.5:status" TEXT,
  "Q-7.5:score" INTEGER,
  "Q-7.8:status" TEXT,
  "Q-7.8:score" INTEGER,
  "Q-7.4:status" TEXT,
  "Q-7.4:score" INTEGER,
  "ruleCatalog:score" INTEGER
);
.mode csv
.import ${OUTPUT_DIR}/edm-ddb.csv issue
.import ${OUTPUT_DIR}/marc.csv issue
.import ${OUTPUT_DIR}/dc.csv issue
.import ${OUTPUT_DIR}/lido.csv issue
.import ${OUTPUT_DIR}/mets-mods.csv issue
DELETE FROM issue WHERE recordId = 'recordId';

CREATE INDEX "file1_idx" ON "files" ("file");
CREATE INDEX "schema_idx" ON "files" ("schema");
CREATE INDEX "set_id_idx" ON "files" ("set_id");
CREATE INDEX "provider_id_idx" ON "files" ("provider_id");

CREATE INDEX "i_recordId" ON "issue" ("recordId");
CREATE INDEX "i_providerid" ON "issue" ("providerid");
CREATE INDEX "Q-1.1:status" ON "issue" ("Q-1.1:status");
CREATE INDEX "Q-1.1:score" ON "issue" ("Q-1.1:score");
CREATE INDEX "Q-1.5:status" ON "issue" ("Q-1.5:status");
CREATE INDEX "Q-1.5:score" ON "issue" ("Q-1.5:score");
CREATE INDEX "Q-2.1:status" ON "issue" ("Q-2.1:status");
CREATE INDEX "Q-2.1:score" ON "issue" ("Q-2.1:score");
CREATE INDEX "Q-2.2:status" ON "issue" ("Q-2.2:status");
CREATE INDEX "Q-2.2:score" ON "issue" ("Q-2.2:score");
CREATE INDEX "Q-2.4:status" ON "issue" ("Q-2.4:status");
CREATE INDEX "Q-2.4:score" ON "issue" ("Q-2.4:score");
CREATE INDEX "Q-2.5:status" ON "issue" ("Q-2.5:status");
CREATE INDEX "Q-2.5:score" ON "issue" ("Q-2.5:score");
CREATE INDEX "Q-2.6:status" ON "issue" ("Q-2.6:status");
CREATE INDEX "Q-2.6:score" ON "issue" ("Q-2.6:score");
CREATE INDEX "Q-3.1:status" ON "issue" ("Q-3.1:status");
CREATE INDEX "Q-3.1:score" ON "issue" ("Q-3.1:score");
CREATE INDEX "Q-3.2:status" ON "issue" ("Q-3.2:status");
CREATE INDEX "Q-3.2:score" ON "issue" ("Q-3.2:score");
CREATE INDEX "Q-3.3:status" ON "issue" ("Q-3.3:status");
CREATE INDEX "Q-3.3:score" ON "issue" ("Q-3.3:score");
CREATE INDEX "Q-3.5:status" ON "issue" ("Q-3.5:status");
CREATE INDEX "Q-3.5:score" ON "issue" ("Q-3.5:score");
CREATE INDEX "Q-6.1:status" ON "issue" ("Q-6.1:status");
CREATE INDEX "Q-6.1:score" ON "issue" ("Q-6.1:score");
CREATE INDEX "Q-6.3:status" ON "issue" ("Q-6.3:status");
CREATE INDEX "Q-6.3:score" ON "issue" ("Q-6.3:score");
CREATE INDEX "Q-6.5:status" ON "issue" ("Q-6.5:status");
CREATE INDEX "Q-6.5:score" ON "issue" ("Q-6.5:score");
CREATE INDEX "Q-7.1:status" ON "issue" ("Q-7.1:status");
CREATE INDEX "Q-7.1:score" ON "issue" ("Q-7.1:score");
CREATE INDEX "Q-7.3:status" ON "issue" ("Q-7.3:status");
CREATE INDEX "Q-7.3:score" ON "issue" ("Q-7.3:score");
CREATE INDEX "Q-7.5:status" ON "issue" ("Q-7.5:status");
CREATE INDEX "Q-7.5:score" ON "issue" ("Q-7.5:score");
CREATE INDEX "Q-7.8:status" ON "issue" ("Q-7.8:status");
CREATE INDEX "Q-7.8:score" ON "issue" ("Q-7.8:score");
CREATE INDEX "Q-7.4:status" ON "issue" ("Q-7.4:status");
CREATE INDEX "Q-7.4:score" ON "issue" ("Q-7.4:score");
CREATE INDEX "ruleCatalog:score" ON "issue" ("ruleCatalog:score");
EOF

sqlite3 ${OUTPUT_DIR}/ddb.sqlite  << EOF
.mode csv
.headers 'on'
.output ${OUTPUT_DIR}/all-issues.csv
SELECT f.schema, f.set_id, f.provider_id, f.file, i.*
  FROM issue AS i
  LEFT JOIN file_record AS r ON (r.recordId = i.recordId)
  LEFT JOIN files AS f USING(file);
EOF

Rscript scripts/R/process-all.R ${OUTPUT_DIR}

sqlite3 ${OUTPUT_DIR}/ddb.sqlite  << EOF
.mode csv
DROP TABLE IF EXISTS variability;
.import ${OUTPUT_DIR}/variability.csv variability
DROP TABLE IF EXISTS frequency;
.import ${OUTPUT_DIR}/frequency.csv frequency
DROP TABLE IF EXISTS count;
.import ${OUTPUT_DIR}/count.csv count
EOF
