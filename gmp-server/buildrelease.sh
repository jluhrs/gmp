#!/bin/sh
#==========================
# bash - find path to script
#==========================
abspath=$(cd ${0%/*} && echo $PWD/${0##*/})

# to get the path only - not the script name - add
script_path=`dirname "$abspath"`

cd $script_path/../
mvn clean install
cd $script_path
mvn -Pproduction doxygen:report assembly:assembly