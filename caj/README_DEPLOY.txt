This project is a wrapper to convert the CAJ library into an OSGI bundle that can
be used in an OSGi container and at the same time be imported from the
common maven repository

To upgrade the library you need to do the following

1. Update the library in the local maven repository at
http://build.cl.gemini.edu:8081/artifactory

You need to deploy the jar file under the ext-releases-local path putting the
version number as needed.

2. Update the new version in the pom.xml under:

    <properties>
       ...   
       <wrapped.version>1.1.5b</wrapped.version>
    </properties>

3. Test drive it doing mvn package. This should produce a jar file under
target/caj-${version}.jar that contains the OSGi headers

4. Make sure your credentials for the maven repository are set under
~/.m2/settings.xml like:
<?xml version="1.0" encoding="UTF-8"?>
<settings>
  <servers>
    <server>
      <id>gemini-maven</id>
      <username>cquiroz</username>
      <password>..</password>
    </server>
  </servers>
</settings> 

5. Deploy to the repository doing mvn deploy

6. Now you can refer to the OSGi bundle from other projects using the
groupId edu.gemini.giapi.external

7. It is probably wise to clean your externals local maven cache by
rm -R ~/.m2/repository/edu/gemini/giapi/external/
