#!/bin/bash
echo "Environment variables";
echo "---------------------";
echo "\$MQAF_DATA=$MQAF_DATA";
echo "\$MQAF_FTP_USER=$MQAF_FTP_USER";
echo "\$MQAF_FTP_PW=$MQAF_FTP_PW";
echo "\$MQAF_DB_HOST=$MQAF_DB_HOST";
echo "\$MQAF_DB_PORT=$MQAF_DB_PORT";
echo "\$MQAF_DB_DATABASE=$MQAF_DB_DATABASE";
echo "\$MQAF_DB_USER=$MQAF_DB_USER";
echo "\$MQAF_DB_PASSWORD=$MQAF_DB_PASSWORD";
echo "\$MQAF_SOLR_HOST=$MQAF_SOLR_HOST";
echo "\$MQAF_SOLR_PORT=$MQAF_SOLR_PORT";
echo "\$MQAF_VALIDATION_PARAMS=$MQAF_VALIDATION_PARAMS";

echo "Create folder ${MQAF_DATA}/input and ${MQAF_DATA}/output ...";
mkdir -p ${MQAF_DATA}/input ${MQAF_DATA}/output

echo "Copy Metadata-qa-ddb to (writeable) temp directory ..."
cp -R /opt/metadata-qa-ddb /tmp/metadata-qa-ddb

URL=http://${MQAF_SOLR_HOST}:${MQAF_SOLR_PORT}/solr/admin/cores?action=STATUS
HTTP_CODE=$(curl -s -o /dev/null -I -w '%{http_code}' "$URL")
# if [[ "${HTTP_CODE}" = "200" ]]; then
#   ./run-all.sh
# fi

echo "HTTP_CODE: ${HTTP_CODE}"
until [ "${HTTP_CODE}" = "200" ]; do
  echo "waiting for Apache Solr..."
  sleep 5
  HTTP_CODE=$(curl -s -o /dev/null -I -w '%{http_code}' "$URL")
  echo "HTTP_CODE: ${HTTP_CODE}"
done
echo "#"
echo "Apache Solr is ready, starting application..."
echo "#"
sleep 5
./run-all.sh
# echo "Add Prefect deployment ..."
# cd /tmp/metadata-qa-ddb && prefect deployment build prefect2_workflow.py:main_flow -a -n metadata-qa
