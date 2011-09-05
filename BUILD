
1. Requirements
---------------
To build the project you need to install the following:

- Java JDK 1.6 or higher
- Maven 2, version 2.2.0 or higher.

2. Build and deploy locally
---------------------------
At the top level run
   mvn install

This command will go through all the modules and install them in the local
maven repository (at ~/.m2/repository). The install process will download
any required dependencies, compile the code (under src/main/java), compile 
and run the tests (under src/test/java), and package the jar file with
the necessary OSGi headers.

The headers are derived from the pom information and those provided in the
osgi.bnd file

3. Build a single module
------------------------
You can build and deploy locally a single module by issuing a
   mvn install

in a single module

TIP: How to skip the tests
During development we often don't want to run the tests all the time 
You can skip them by issuing:
   mvn -Dmaven.test.skip=true install

4. Integration tests
------------------------
Most tests in the project are unit test but there are some integration tests are
identified by classes ending with IT unlike unit tests that end in Test.

Integration tests are not run by default to make the builds faster and the
normal mvn test target won't execute them.

To run the integration tests manually you need to issue:
    mvn install failsafe:integration-test failsafe:verify

If you wish to run a single integration test you can add to the command line
the system variable it.test with the classname of the test you want to write like

mvn -Dit.test=edu.gemini.aspen.integrationtests.GDSEndToEndIT install failsafe:integration-test failsafe:verify

For more options check
http://maven.apache.org/plugins/maven-failsafe-plugin/examples/single-test.html

5. Use with IntelliJ idea
------------------------------
Idea works best by just importing the pom.xml as a project file definition

6. Generate application
-----------------------
Applications are just other modules that define a list of bundles to
deploy and configuration. They use the assembly plugin and will produce 
a zip file with all the required bundles and configurations.

As an example go to gmp-server and read the instructions on how to build
the application there

7. Additional Documentation
--------------------------
This project comes with a set of documentation that can be generated via doxygen.

To produce the documentation, go to the gmp-server directory and type:

    mvn resources:copy-resources doxygen:report

and then open the generated documentation at gmp-server/target/site/doxygen/index.html
