This project is a wrapper to embed the CAJ library into an OSGI. The
libraries are loaded from the common maven repository and embedded
into this bundle

To upgrade any of the libraries wrapped you need to do the following

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
target/jac-lib-${version}.jar that contains the OSGi headers

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
groupId edu.gemini.aspen
