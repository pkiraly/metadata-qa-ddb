#!/bin/bash
echo "Environment variables";
echo "---------------------";
echo "\$MQA_DATA=$MQA_DATA";
echo "\$MQA_FTP_USER=$MQA_FTP_USER";
echo "\$MQA_FTP_PW=$MQA_FTP_PW";
echo "\$MQA_MY_HOST=$MQA_MY_HOST";
echo "\$MQA_MY_PORT=$MQA_MY_PORT";
echo "\$MQA_MY_DB=$MQA_MY_DB";
echo "\$MQA_MY_USER=$MQA_MY_USER";
echo "\$MQA_MY_PASSWORD=$MQA_MY_PASSWORD";
echo "\$MQA_SOLR_HOST=$MQA_SOLR_HOST";
echo "\$MQA_SOLR_PORT=$MQA_SOLR_PORT";
echo "\$MQA_VALIDATION_PARAMS=$MQA_VALIDATION_PARAMS";

echo "Create folder ${MQA_DATA}/input and ${MQA_DATA}/output ...";
mkdir -p ${MQA_DATA}/input ${MQA_DATA}/output

echo "Copy Metadata-qa-ddb to (writeable) temp directory ..."
cp -R /opt/metadata-qa-ddb /tmp/metadata-qa-ddb

# echo "Add Prefect deployment ..."
# cd /tmp/metadata-qa-ddb && prefect deployment build prefect2_workflow.py:main_flow -a -n metadata-qa
