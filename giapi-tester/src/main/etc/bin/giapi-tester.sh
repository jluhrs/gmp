#!/bin/bash
#==========================
# GIAPI Tester
#==========================
#
# Usage:
#   ./giapi-tester.sh -?

# Var setup
# Don't allow non initialized vars
set -o nounset

# Exit on failure
set -e

# Check that java is available
which java > /dev/null || { echo "Need java in PATH to run"; exit 1; }

# Find path to script
#==========================
#Find the dir the scrip is located, no matter if there are symlinks
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ] ; do SOURCE="$(readlink "$SOURCE")"; done
SCRIPT_PATH="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

pushd $SCRIPT_PATH > /dev/null

GIAPI_TESTER_JAR=$SCRIPT_PATH/giapi-tester.jar

if ! [ -e $GIAPI_TESTER_JAR ]; then
    echo "Giapi tester jar file not found, review your installation"
    exit 1
fi

exec java -jar $GIAPI_TESTER_JAR "$@"

popd > /dev/null
