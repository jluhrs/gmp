The Gemini Master Process (GMP), the communication broker in the GIAPI model

GMP Version: ${gmp.version}

PACKAGE
-------
The GMP is delivered as tar.gz or .zip package containing all the binaries and
configuration required to run the server

The following details the files and directories included in the package

/README.txt
    This file

/bin
    Scripts and binaries to launch the GMP server
/bin/start.sh
    Bash script used to control the GMP
/bin/gmp-server
    Bash script used to control the GMP
/bin/pax-runner-1.7.4.jar
    Pax runner binary is a launcher for OSGi applications
/bin/runner.args
    Arguments passed to pax-runner
/bundles
    All the bundles that compose the gmp server stored as a maven repository
/conf
    Configuration files
/conf/services
    Configuration files of specific OSGi services. These files are observed at
    runtime and modifications are reflected on the running services
/felix
    OSGi container
/logs
    Directory with the application log
/doc
    HTML documentiation

When the application is launched a directory under /bin/runner is created that
contains a cache of the binaries. The directory can be deleted freely

REQUIREMENTS
------------
The only set requirement is a Java 1.6 or higher installation. You can verify it
with the command:

    java -version

RUNNING STANDALONE
------------------
To start the GMP go to the /bin directory and invoke:

 ./start.sh

At the end of the start the process should be running and there will be a prompt
in the screen.

Type:

  lb

to see all the services that are running. You should get something similar to:

g! lb

START LEVEL 6
   ID|State      |Level|Name
    0|Active     |    0|System Bundle (3.0.1)
    1|Active     |    4|Gemini JMS API (1.2.0)
    ...
   50|Active     |    1|Apache Felix Gogo Shell (0.6.0)


Type help to see the list of other commands.

RUNNING AS DAEMON
-----------------
A script is provided to run the GMP in daemon mode, to do so invoke:
 ./gmp-server start 

This will launch the gmp-server in the background

TELNET
------
A telnet daemon is included that gives access to the shell with several commands
that can be used to manage the application. To reach the server type

    telnet localhost 15001

To exit the console press CTRL+D

NOTE: There is a bug in the telnet daemon, if you press CTRL+C to exit the
telnet client will hang. This doesn't affect the GMP.

NOTE2: There is no security, anybody can reach the telnet daemon

DOCS
----
The documentation in HTML format is available at the docs dir or through the local 
web server at
    http://localhost:8888/docs/

LOGS
----
The main log file can be found at logs/gmp.log

The log file is rotated daily

A second file contains the results of the standard output if gmp is run as a daemon.
The file is located at logs/gmp-server.out

WEB CONSOLE
-----------
A Web interface to the GMP OSGi container is available at

http://localhost:8888/admin
