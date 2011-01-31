#!/bin/bash
#==========================
# bash - find path to script
#==========================
abspath=$(cd ${0%/*} && echo $PWD/${0##*/})

# to get the path only - not the script name - add
script_path=`dirname "$abspath"`

# Will start pax-runner reading the configuration from the file bin/runner.args
exec java -jar $script_path/pax-runner-1.5.0.jar