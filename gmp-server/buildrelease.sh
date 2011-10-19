#!/bin/sh
#==========================
# bash - find path to script
#==========================
abspath=$(cd ${0%/*} && echo $PWD/${0##*/})

# to get the path only - not the script name - add
script_path=`dirname "$abspath"`

# Install gmp-server pom
cd $script_path
mvn install

# Do a full install
cd $script_path/../
mvn clean install

# Do the documentation
cd $script_path
mvn -Pproduction resources:copy-resources doxygen:report assembly:assembly rpm:rpm
