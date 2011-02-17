INTRO
-----

This module represents the GMP server and its pom.xml describes the set of bundles that compose the application as well
as some configurations. From here one can build a locally running application or deploy a zip file or assembly in maven
parlance.

NOTE: All the instructions assume that your bundles sit in a GMP_SRC_DIR

BUNDLES
-------
From the point of view of the application there are 2 types of bundles:

- GMP Bundles:
For those the source code is under GMP_SRC_DIR and to be used they have to be built and installed in the local
maven repository. To do so you need to go to GMP_SRC_DIR and issue mvn install. Note that unless you install them the
latest version of them won't be reflected in the GMP Server

The list of GMP bundles in the GMP server applications are dependencies on the local pom at GMP_SRC_DIR/gmp-server/pom.xml
, for example GIAPI, JmsProvider, etc. are listed as dependencies.

- External dependencies
External dependencies are marked as provisioned and listed at GMP_SRC_DIR/provision/pom.xml

CONFIGURATION
-------------
As much as possible the standard ConfigAdmin service is used for configuration. The configurations are stored at
GMP_SRC_DIR/gmp-server/src/main/etc/conf/services in files named [ServicePID].cfg

A few bundles need the configuration in form of system properties. For those the pom.xml defines them in the
<properties> section

ADDING APP BUNDLES
------------------
If you need to add an extra bundle to the application you can put it in the local pom as a regular maven dependency and
then deploy the pom via a:

   cd GMP_SRC_DIR/gmp-server
   mvn install
for the local repository, or:

   cd GMP_SRC_DIR/gmp-server
   mvn deploy
for the global repository

ADDING EXTERNAL BUNDLES
-----------------------
If you need to add an extra external bundle like OSGi services, third party bundles, or in general any bundle that is
not used at compile time by other bundles should be added to the provision file at GMP_SRC_DIR/provision/pom.xml
and then then deploy the pom via a:

   cd GMP_SRC_DIR/provision
   mvn install
for the local repository, or:

   cd GMP_SRC_DIR/provision
   mvn deploy
for the global repository

RUN LOCALLY
-----------
To run the application locally you need to install pax-runner and set it so that pax-run.sh/pax-run.bat is available
in your PATH. You can get pax-runner at
http://repo1.maven.org/maven2/org/ops4j/pax/runner/pax-runner-assembly/1.5.0/pax-runner-assembly-1.5.0-jdk15.tar.gz

Untar the file and add the pax-runner-1.5.0/bin to the global PATH

Then you can run it using the included startgmp.sh script

Then felix is downloaded and installed into the runner directory and the bundles obtained from the local repository
After completed all the bundles are running locally and some extra services are included like ConfigAdmin, LogService
and the WebConsole

Note that changes to the set of bundles are not detected immediately by pax-runner. You need to do a mvn install and then
start gmp again for those changes to become visible.

Changes to the configuration under GMP_SRC_DIR/gmp-server/src/main/etc/conf/services are seen immediately

The startgmp.sh script is set to keep the links between the bundles and the mvn repository so you can update your
bundles at runtime with maven using:
   mvn install

And then in the felix console do an update via
   update [bundleid]

GMP CONSOLE
-----------
The GMP Server starts 2 consoles:
- Telnet console at localhost port 15001

- The WebConsole at http://localhost:8888/admin

DEPLOYMENT PACKAGE
------------------
To build a tar ball with the application so that it can be distributed without access to the maven repositories do:

    mvn -Pproduction assembly:assembly

The result file will be under target/gmp-server-{VERSION}.tar.gz

The tar file contains all the bundles, configuration and dependencies to run and it shouldn't require network connection
to our local maven repository to run


