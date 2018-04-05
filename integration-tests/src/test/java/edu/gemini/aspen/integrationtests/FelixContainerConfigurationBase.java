package edu.gemini.aspen.integrationtests;

import org.ops4j.pax.exam.Option;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.lang.reflect.Array;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.*;

abstract public class FelixContainerConfigurationBase {
    @Inject
    protected BundleContext context;

    public <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    Option[] baseContainerConfig() {
        return options(
                systemProperty("felix.fileinstall.dir").value(System.getProperty("basedir") + confDir()),
                systemProperty("felix.fileinstall.noInitialDelay").value("true"),
                frameworkProperty("org.osgi.framework.system.capabilities").value("osgi.ee; osgi.ee=\"JavaSE\";version:List=\"1.0,1.1,1.2,1.3.0,1.4,1.5.0,1.6,1.7,1.8\""),
                frameworkProperty("felix.cache.locking").value("false"),
                systemPackage("javax.xml.parsers"),
                systemPackage("org.w3c.dom"),
                systemPackage("javax.xml.datatype"),
                systemPackage("javax.xml.namespace"),
                systemPackage("javax.xml.transform"),
                systemPackage("javax.xml.transform.dom"),
                systemPackage("javax.xml.transform.sax"),
                systemPackage("javax.xml.transform.stream"),
                systemPackage("javax.xml.stream"),
                systemPackage("javax.xml.validation"),
                systemPackage("javax.management"),
                systemPackage("javax.net.ssl"),
                systemPackage("org.xml.sax"),
                systemPackage("org.xml.sax.helpers"),
                systemPackage("org.xml.sax.ext"),
                junitBundles(),
                mavenBundle().artifactId("guava").groupId("com.google.guava").versionAsInProject(),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").versionAsInProject(),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").versionAsInProject(),
                mavenBundle().artifactId("org.apache.felix.configadmin").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("org.apache.felix.fileinstall").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("javax.transaction").groupId("org.glassfish").version("3.1.1"),
                mavenBundle().artifactId("javax.jms").groupId("org.glassfish").version("3.1.1"),
                mavenBundle().artifactId("jaxb-api").groupId("javax.xml.bind").version("2.3.0"),
                mavenBundle().artifactId("osgi").groupId("edu.gemini.util.osgi").versionAsInProject(),
                mavenBundle().artifactId("scala-library").groupId("org.scala-lang").versionAsInProject(),
                mavenBundle().artifactId("javax.activation").groupId("org.glassfish").version("3.0-Prelude"),
                mavenBundle().artifactId("gmp-top").groupId("edu.gemini.gmp").update().versionAsInProject()
        );
    }

    abstract protected String confDir();

    Bundle getBundle(String symbolicName) {
        for (Bundle b : context.getBundles()) {
            if (symbolicName.equals(b.getSymbolicName())) {
                return b;
            }
        }
        fail();
        return null;
    }
}
