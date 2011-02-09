The Gemini Master Process (GMP), the communication broker in the GIAPI model

PACKAGE
-------
The GMP is delivered as tar.gz or .zip package containing all the binaries and configuration required to run the server

The following details the files and directories included in the package

/README.txt
    This file

/bin
    Scripts and binaries to launch the GMP server
/bin/start.sh
    Bash script used to start the GMP
/bin/pax-runner-1.5.0.jar
    Pax runner binary is a launcher for OSGi applications
/bin/runner.args
    Arguments passed to pax-runner
/bundles
    All the bundles that compose the gmp server stored as a maven repository
/conf
    Configuration files
/conf/services
    Configuration files of specific OSGi services. These files are observed at runtime and modifications are
    reflected on the running services
/felix
    OSGi container
/logs
    Directory with the application log

When the application is launched a directory under /bin/runner is created that contains a cache of the binaries
The directory can be deleted freely

REQUIREMENTS
------------
The only set requirement is a Java 1.6 or higher installation. You can verify it with the command:
java -version

RUNNING
-------
To start the GMP go to the /bin directory and call the start.sh script

At the end of the start the process should be running and there will be a prompt in the screen.
Type help to see the list of commands

TELNET
------
A telnet daemon is included that gives access to the shell with several commands that can be used to manage
the application. To reach the server type

telnet hostname 15001

NOTE: There is a bug in the telnet daemon, if you press CTRL+C to exit the telnet client will hang. This doesn't
affect the GMP

NOTE2: There is no security, anybody can reach the telnet daemon

WEB CONSOLE
-----------
A Web interface to the GMP OSGi container is available at

http://hostname:8888/admin
