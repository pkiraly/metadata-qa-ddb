# Metadata quality assessment of Deutsche Digitale Bibliothek metadata

A metadata quality assessment tool customized for Deutsche Digitale Bibliothek's requirements against the incomming metadata records. This is an extension of [Metadata Quality Assessment Framework](https://github.com/pkiraly/metadata-qa-api).

# Installation

The software depends on the following technologies:

* MySQL
* SQLite3
* Java 11
* R
* Apache Solr
+ PHP

The following installation instructions work in Ubuntu. These contain simplified steps, for further details please consult the official documentation of these tools.

## Auxiliary tools

```
sudo apt install jq wget curl
```

## MySQL

```
sudo apt install mysql-server
sudo service mysql start
```

## SQLite 3

```
sudo apt install sqlite3
```

## Java

```
sudo apt install openjdk-11-jre-headless
```

## R

```
wget -qO- https://cloud.r-project.org/bin/linux/ubuntu/marutter_pubkey.asc | sudo gpg --dearmor -o /usr/share/keyrings/r-project.gpg
echo "deb [signed-by=/usr/share/keyrings/r-project.gpg] https://cloud.r-project.org/bin/linux/ubuntu jammy-cran40/" | sudo tee -a /etc/apt/sources.list.d/r-project.list
sudo apt update
sudo apt install --no-install-recommends r-base r-cran-tidyverse r-cran-stringr r-cran-gridextra
```

## Apache Solr

```
export SOLR_VERSION=9.1.1
cd /opt
curl -s -L https://archive.apache.org/dist/solr/solr/${SOLR_VERSION}/solr-${SOLR_VERSION}.tgz --output solr-${SOLR_VERSION}.tgz
tar zxf solr-${SOLR_VERSION}.tgz
rm solr-${SOLR_VERSION}.tgz
ln -s solr-${SOLR_VERSION} solr
```

run Apache Solr

```
/opt/solr/bin/solr start -m 2g
```

## PHP

```
sudo apt install php php-http-request2 php-mysql php-sqlite3
```

# Installing the software


Download and unzip the release package.

```
wget https://github.com/pkiraly/metadata-qa-ddb/releases/download/v1.0.0/metadata-qa-ddb-1.0.0-release.zip
unzip metadata-qa-ddb-1.0.0-release.zip
cd metadata-qa-ddb-1.0.0-release
```
 
# Configuration

Set the configuration file.

1. Create a configuration file:
```
cp configuration.cnf.template configuration.cnf
```
2. Edit the configuration file

```
INPUT_DIR=<path to input directory>
OUTPUT_DIR=<path to output directory>

# FTP user name and password for the DDB FTP server
FTP_USER=<FTP user name>
FTP_PW=<FTP password>

# MySQL database settings
MY_HOST=localhost
MY_PORT=3306
MY_DB=<MySQL database name>
MY_USER=<MySQL user name>
MY_PASSWORD=<MySQL password>
# the type of configuration when call 'mysql' command. 
# valid values: LOCAL_FILE, TEMP_FILE or ENVIRONMENT_VARIABLES
MYSQL_CONFIG_TYPE=LOCAL_FILE

# Apache Solr settings
SOLR_HOST=localhost
SOLR_PORT=8983
```

Log in to MySQL, create a database and a dedicated user:

```
CREATE DATABASE ddb;

CREATE USER '<user name>'@'localhost' IDENTIFIED BY '<password>';
GRANT ALL PRIVILEGES ON ddb.* TO '<user name>'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;
```

# Running the software

## Download files and import file information into the database

create the database tables
```
scripts/create_database.mysql.sh
```

download files from FTP server
```
scripts/ingest/01_download_from_ftp.sh
```

unzip the downloaded zip files
```
scripts/ingest/02_extract_downloaded_files.sh
```

extract file info (path, size etc.) from the directory. The file paths contain semantic information about the data providers. The following data elements are extracted: file path, metadata schema, provider identifier, provider name, data set identifier, data set name, last modification date, file size. These data are saved into $OUTPUT_DIR/files.csv file.
```
scripts/ingest/03_extract_basic_info_from_downloaded_files.sh
```

import file info into MySQL (it first transforms CSV to SQL)
```
scripts/ingest/04_import_basic_info.mysql.sh
```

(optional) harvest Europeana-EDM records for each data sets
```
scripts/ingest/05_harvest_edm.mysql.sh
```

## Index and store basic data

These commands will index the following fields into Apache Solr: identifier, provider identifier and title. The 'what to index?' question is answered by the schema's yaml definition file at the main/resources directory. The record ID and the container file is stored in MySQL. The record's full XML presentation is stored in a SQLite3 database. It is needed only for the display of records in the web user interface.

index DDB-EDM records
```
scripts/index/01_index_ddb-edm.sh
```

index MARC records
```
scripts/index/02_index_marc.sh
```

index DDB-DC records
```
scripts/index/03_index_ddb-dc.sh
```

index LIDO records
```
scripts/index/04_index_lido.sh
```

index METS-MODS records
```
scripts/index/05_index_mets-mods.sh
```

## Run quality assessment

The quality assessment reads from the `$INPUT_DIR` directory and writes the results to `$OUTPUT_DIR` directory as CSV files, such as `edm-ddb.csv`.

quality assessment of DDB-EDM records
```
scripts/process/01_process_ddb-edm.sh
```

quality assessment of MARC
```
scripts/process/02_process_marc.sh
```

quality assessment of DDB-DC
```
scripts/process/03_process_ddb-dc.sh
```

quality assessment of LIDO
```
scripts/process/04_process_lido.sh
```

quality assessment of METS-MODS
```
scripts/process/05_process_mets-mods.sh
```

## Importing measurement results to database


import DDB-EDM
```
scripts/process/11_import_ddb-edm.mysql.sh
```

import MARC
```
scripts/process/11_import_marc.mysql.sh
```

import DDB-DC
```
scripts/process/11_import_dc.mysql.sh
```

import LIDO
```
scripts/process/11_import_lido.mysql.sh
```

import METS-MODS
```
scripts/process/11_import_mets-mods.mysql.sh
```

calculate aggregated results
```
scripts/process/12_calculate_aggregations.mysql.sh
```
