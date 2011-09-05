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
ABSPATH=$(cd ${0%/*} && echo $PWD/${0##*/})

# to get the path only - not the script name - add
SCRIPT_PATH=`dirname "$ABSPATH"`

GIAPI_TESTER_VERSION=0.1.0

GIAPI_TESTER_JAR=$SCRIPT_PATH/giapi-tester-$GIAPI_TESTER_VERSION-jar-with-dependencies.jar 

if ! [ -e $GIAPI_TESTER_JAR ]; then
    echo "Giapi tester jar file not found, review your installation"
    exit 1
fi

java -jar $GIAPI_TESTER_JAR $@
