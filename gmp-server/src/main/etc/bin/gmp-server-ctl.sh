#!/bin/bash
#
# init.d script to manage a container based on an OSGi framework
#
set -e # stop on errors
#set -u # Don't allow using non defined variables
#
#              <startlevels> <startprio> <stopprio>
# chkconfig:   345 75 15
# description: The gmp-server application
#
# NOTE: the 2 comments above and the following 4 variable values will probably
#       need customization when packaging the distribution. The generated values
#       are meant for local testing
#
# Vars that are set at build time

PAX_RUNNER_VERSION=${pax-runner.version}
GMP_VERSION=${gmp.version}

# Confirm that java is available
which java > /dev/null || { echo "Need java in PATH to run"; exit 1; }

# Verify existing variables
if [ -z $GPI_ROOT ]; then
    GPI_ROOT=/gemsoft/opt/gpi/
    echo "GPI_ROOT not set. Using $GPI_ROOT"
fi
if ! [ -d $GPI_ROOT ]; then
    echo "$GPI_ROOT directory not found"
    exit -1
fi

# Confirm that jps is available
which jps > /dev/null || { echo "Need jps in PATH to run"; exit 1; }

# App variables
app_name=gmp-server
app_root=${GPI_ROOT}/gmp-server-$GMP_VERSION
pid_file=${app_root}/bin/${app_name}.pid
log_file=${app_root}/logs/${app_name}.out

# pax-runner needs this dir to run
if ! [ -d ~software/.pax/runner ]; then
    mkdir -p ~software/.pax/runner
    touch ~software/.pax/runner/org.ops4j.pax.runner.daemon.password.file
fi


#
# - get PID from .pid file if it exists and check if it is running
#
pid=`[[ -e ${pid_file} ]] && cat ${pid_file} || echo "NO_PID_FILE"`
set +e # disable because egrep will return 1 on mismatch
pid_isrunning=`ps -eo pid | egrep ^[[:space:]]*${pid}$`
set -e # reenable
#
# Start the GMP server if not already running
#
function startContainer() {
    if [ -z ${pid_isrunning} ]; then
        echo "Starting ${app_name} version $GMP_VERSION"
        # Will start pax-runner as a daemon reading the configuration from the file bin/runner.args
        pushd ${app_root}/bin > /dev/null;
        java -cp ${app_root}/bin/pax-runner-${PAX_RUNNER_VERSION}.jar org.ops4j.pax.runner.daemon.DaemonLauncher --startd &> ${log_file}
        wait $!
        sleep 4
        popd > /dev/null
        # Get the pid with jps
        jps -l | grep "org.apache.felix.main.Main" | sed "s/[[:space:]]*\([\d]*\) .*/\1/" > ${pid_file}
        retval=$?
        sleep 10
        echo "Started ${app_name}"
        return $retval
    else
        echo "${app_name} already running with pid ${pid}"
    fi
}

#
# Gracefully stop the GMP
#
function stopContainer() {
  if [ ! -z ${pid_isrunning} ]; then
    echo "Stopping ${app_name} with pid ${pid}"
    exec java -cp ${app_root}/bin/pax-runner-${PAX_RUNNER_VERSION}.jar org.ops4j.pax.runner.daemon.DaemonLauncher --stop
    wait $!
    retval=$?
    sleep 10
    return $retval
  else
    echo "${app_name} not running"
  fi
}


#
# Kill the GMP process
#
function killContainer() {
  if [ ! -z ${pid_isrunning} ]; then
    echo "Killing ${app_name} with pid ${pid}"
    kill `cat ${pid_file}`
  else
    echo "${app_name} not running"
  fi
}

#
# Check if GMP is still running
#
function containerStatus() {
  if [ ! -z ${pid_isrunning} ]; then
    echo "${app_name} is running with pid ${pid}"
  else
    echo "${app_name} not running"
  fi
}

# Reenable as the script could called without parameters
set +u

#
# - main command dispatch ...
#
case "$1" in
    start)
      startContainer
      ;;
    stop)
      stopContainer
      ;;
    kill)
      killContainer
      ;;
    #restart)
    #  stopContainer
    #  startContainer
    #  ;;
    status)
      containerStatus
      ;;
    *)
      echo "Usage: ${app_name} {start|stop|status|kill}"
      exit 1
      ;;
esac
# End of main
exit 0