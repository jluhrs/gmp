package edu.gemini.aspen.integrationtests;

import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.cleanCaches;

abstract public class FelixContainerConfigurationBase {
    @Inject
    protected BundleContext context;

    @Configuration
    public Option[] baseContainerConfig() {
        return options(
                felix(),
                cleanCaches(),
                systemProperty("felix.fileinstall.dir").value(System.getProperty("basedir") + confDir()),
                systemProperty("felix.fileinstall.noInitialDelay").value("true"),
                mavenBundle().artifactId("org.apache.felix.ipojo").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("org.apache.felix.ipojo.annotations").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("com.springsource.javax.jms").groupId("javax.jms").versionAsInProject(),
                mavenBundle().artifactId("org.apache.felix.configadmin").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("org.apache.felix.fileinstall").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").versionAsInProject(),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").versionAsInProject(),
                mavenBundle().artifactId("guava-osgi").groupId("com.googlecode.guava-osgi").versionAsInProject(),
                mavenBundle().artifactId("shared-test").groupId("gemini-nocs").update().versionAsInProject(),
                mavenBundle().artifactId("shared-util").groupId("gemini-nocs").update().versionAsInProject());
    }

    abstract protected String confDir();

    protected Bundle getBundle(String symbolicName) {
        for (Bundle b : context.getBundles()) {
            if (symbolicName.equals(b.getSymbolicName())) {
                return b;
            }
        }
        fail();
        return null;
    }
}
