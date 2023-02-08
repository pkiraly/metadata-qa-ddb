#!/usr/bin/env bash

source ./configuration.cnf
SOLR_BASE_URL=http://${SOLR_HOST:-localhost}:${SOLR_PORT:-8983}

check_core() {
  LOCAL_CORE=$1
  LOCAL_URL=$(printf "%s/solr/admin/cores?action=STATUS&core=%s" $SOLR_BASE_URL $LOCAL_CORE)
  CORE_EXISTS=$(curl -s "$LOCAL_URL" | jq .status | grep "\"$LOCAL_CORE\":" | grep -c -P '{$')
  # use echo instead of return
  echo ${CORE_EXISTS}
}

create_core() {
  LOCAL_CORE=$1
  echo "creating Solr index: ${LOCAL_CORE} at $SOLR_HOST"
  curl -s "$SOLR_BASE_URL/solr/admin/cores?action=CREATE&name=$LOCAL_CORE&configSet=_default"
}

initialize() {
  LOCAL_CORE=$1

  PROD_EXISTS=$(check_core $LOCAL_CORE)
  echo "${LOCAL_CORE} exists: ${PROD_EXISTS}"
  if [[ $PROD_EXISTS -ne 1 ]]; then
    echo "Create Solr core '$LOCAL_CORE'"
    create_core $LOCAL_CORE
  else
    curl -s ${SOLR_BASE_URL}/solr/${LOCAL_CORE}/update \
      -H "Content-type: text/xml" \
      --data-binary '<delete><query>*:*</query></delete>'
    curl -s "${SOLR_BASE_URL}/solr/${LOCAL_CORE}/update?optimize=true" \
      -H 'Content-type: text/xml' \
      --data-binary '<commit/>'
  fi
}
