#!/bin/bash
#==========================
# bash - find path to script
#==========================
abspath=$(cd ${0%/*} && echo $PWD/${0##*/})

# to get the path only - not the script name - add
script_path=`dirname "$abspath"`

#exec java -jar -Dfelix.config.properties=file:conf/bundle.properties felix/org.apache.felix.main-3.0.2.jar
exec java -jar