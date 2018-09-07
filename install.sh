#!/usr/bin/env sh

set -e
RELEASE_DIR="release"
RELEASE_SINK_FILE="${RELEASE_DIR}/kubesecret.darwin-amd64"

INSTALL_SINK_FILE="/usr/local/bin/kubesecret.darwin-amd64"

echo "==== install release ===="
echo " - from: ${RELEASE_SINK_FILE}"
echo " - to: ${INSTALL_SINK_FILE} "

set -ex
cp -rf ${RELEASE_SINK_FILE} ${INSTALL_SINK_FILE}

${RELEASE_SINK_FILE} --help

