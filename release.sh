#!/usr/bin/env sh
set -e

COMPILER_SINK_FILE="build/kubesecret.darwin-amd64"
RELEASE_DIR="release"
RELEASE_SINK_FILE="${RELEASE_DIR}/kubesecret.darwin-amd64"

echo "==== install release ===="
echo " - from: ${COMPILER_SINK_FILE}"
echo " - to: ${RELEASE_SINK_FILE} "

cp -rf ${COMPILER_SINK_FILE} ${RELEASE_SINK_FILE}

echo "=== contents of ${RELEASE_DIR} ... ==="
echo ""
ls -ls ${RELEASE_DIR}

echo ""
echo "=== try it ... =="
set -ex
${RELEASE_SINK_FILE} --help

