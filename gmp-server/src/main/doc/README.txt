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
It can be deleted freely

RUNNING
-------