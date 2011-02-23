The Gemini Master Process (GMP), the communication broker in the GIAPI model

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
    Configuration files of specific OSGi services. These files are observed at
    runtime and modifications are reflected on the running services
/felix
    OSGi container
/logs
    Directory with the application log

When the application is launched a directory under /bin/runner is created that
contains a cache of the binaries. The directory can be deleted freely

REQUIREMENTS
------------
The only set requirement is a Java 1.6 or higher installation. You can verify it
with the command:

    java -version

RUNNING
-------
To start the GMP go to the /bin directory and invoke:

 ./start.sh

Note: Don't try to use "sh start.sh" as that will prevent the script to locate
      its current working directory.

Note 2: This version does not support running the service as a daemon. This
        will be fixed in the next version.

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
    2|Active     |    4|ActiveMQ Broker Service (1.1.0)
    3|Active     |    4|ActiveMQ JMS Service Provider (1.1.0)
    4|Active     |    4|JCA Libraries (1.0.0)
    5|Active     |    4|EPICS Service (0.2.0)
    6|Active     |    4|Channel Access Server (0.1.4)
    7|Active     |    4|GIAPI (0.9.0)
    8|Active     |    4|GIAPI JMS Util (0.10.0)
    9|Active     |    4|GIAPI Status Service (0.1.0)
   10|Active     |    4|GMP GIAPI Services (0.1.0)
   11|Active     |    4|GMP Sequence Commands Service (0.9.0)
   12|Active     |    4|GMP PCS Updater Service (0.2.0)
   13|Active     |    4|GMP TCS Context Service (0.1.0)
   14|Active     |    4|GMP Logging Service (0.1.0)
   15|Active     |    4|GMP Status Database (0.1.0)
   16|Active     |    4|GMP Status Gateway (0.1.0)
   17|Active     |    4|GMP Commands Gateway (0.8.0)
   18|Active     |    4|GMP Epics Access Service (0.1.0)
   19|Active     |    4|GMP Epics Simulator (0.1.0)
   20|Active     |    4|GMP EPICS Status Service (0.1.0)
   21|Resolved   |    4|GMP WebConsole Branding (0.1.0)
   22|Active     |    4|GMP Handlers State Service (0.1.0)
   23|Active     |    2|Apache Felix Shell Service (1.4.2)
   24|Active     |    2|Apache Felix Remote Shell (1.1.0)
   25|Active     |    2|OPS4J Pax Logging - API (1.6.0)
   26|Active     |    2|OPS4J Pax Logging - Service (1.6.0)
   27|Active     |    2|Apache Felix File Install (3.1.10)
   28|Active     |    2|Apache Felix Configuration Admin Service (1.2.8)
   29|Active     |    2|Apache Felix Web Management Console (3.1.6)
   30|Active     |    2|Apache Felix Preferences Service (1.0.4)
   31|Active     |    2|Apache Felix Declarative Services (1.6.0)
   32|Active     |    2|Apache Felix Http Jetty (2.0.4)
   33|Active     |    2|Apache Felix Http Whiteboard (2.0.4)
   34|Active     |    2|Apache Felix Metatype Service (1.0.4)
   35|Active     |    2|Apache Felix iPOJO (1.6.8)
   36|Active     |    2|Apache Felix iPOJO Arch Command (1.6.0)
   37|Active     |    2|Apache Felix iPOJO WebConsole Plugins (1.6.0)
   38|Active     |    2|Java Messaging System API (1.1.0)
   39|Active     |    2|activemq-core (5.4.2)
   40|Active     |    2|kahadb (5.4.2)
   41|Active     |    2|geronimo-j2ee-management_1.1_spec (1.0.1)
   42|Active     |    2|geronimo-annotation_1.0_spec (1.1.1)
   43|Active     |    2|dom4j DOM Processor (1.5.2)
   44|Active     |    2|Java XML Stream API (StAX) (1.0.1)
   45|Active     |    2|Guava: Google Core Libraries for Java 1.5 (8.0.0)
   46|Active     |    2|osgi.cmpn (4.2.0.200908310645)
   47|Active     |    2|Apache Felix Web Console Memory Usage Plugin (1.0.2)
   48|Active     |    1|Apache Felix Gogo Command (0.6.0)
   49|Active     |    1|Apache Felix Gogo Runtime (0.6.0)
   50|Active     |    1|Apache Felix Gogo Shell (0.6.0)


Type help to see the list of other commands.

TELNET
------
A telnet daemon is included that gives access to the shell with several commands
that can be used to manage the application. To reach the server type

    telnet localhost 15001

NOTE: There is a bug in the telnet daemon, if you press CTRL+C to exit the
telnet client will hang. This doesn't affect the GMP.

NOTE2: There is no security, anybody can reach the telnet daemon

WEB CONSOLE
-----------
A Web interface to the GMP OSGi container is available at

http://localhost:8888/admin
