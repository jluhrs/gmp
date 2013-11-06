package edu.gemini.aspen.integrationtests;

import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.cleanCaches;

public class EpicsServiceBaseIntegration {
    static {
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "127.0.0.1");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");
    }

    @Inject
    protected BundleContext context;

    @Configuration
    public static Option[] baseConfig() {
        return options(
                felix(),
                cleanCaches(),
                waitForFrameworkStartup(),
                systemProperty("felix.fileinstall.dir").value(System.getProperty("basedir") + "/src/test/resources/conf/services"),
                systemProperty("felix.fileinstall.noInitialDelay").value("true"),
                systemProperty("ipojo.log.level").value("debug"),
                mavenBundle().artifactId("org.apache.felix.ipojo").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("org.osgi.compendium").groupId("org.osgi").versionAsInProject(),
                mavenBundle().artifactId("org.apache.felix.configadmin").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("org.apache.felix.fileinstall").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").versionAsInProject(),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").versionAsInProject(),
                mavenBundle().artifactId("guava").groupId("com.google.guava").versionAsInProject(),
                mavenBundle().artifactId("caj").groupId("edu.gemini.external.osgi.com.cosylab.epics.caj").versionAsInProject(),
                mavenBundle().artifactId("jca").groupId("edu.gemini.external.osgi.gov.aps.jca").versionAsInProject(),
                mavenBundle().artifactId("epics-api").groupId("edu.gemini.epics").versionAsInProject(),
                mavenBundle().artifactId("epics-service").groupId("edu.gemini.epics").versionAsInProject()
        );
    }

    protected Bundle getEpicsServiceBundle() {
        for (Bundle b : context.getBundles()) {
            if ("edu.gemini.epics.service".equals(b.getSymbolicName())) {
                return b;
            }
        }
        fail("Bundle not found");
        throw new AssertionError();
    }
}
