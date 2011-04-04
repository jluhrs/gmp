package edu.gemini.epics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.cleanCaches;

/**
 * Basic integration test for EpicsService \verifying that the services can run and its properties be set
 */
@RunWith(JUnit4TestRunner.class)
public class EpicsServiceIT {
    @Inject
    private BundleContext context;

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
                mavenBundle().artifactId("org.osgi.compendium").groupId("org.osgi").version("4.2.0"),
                mavenBundle().artifactId("org.apache.felix.configadmin").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("org.apache.felix.fileinstall").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").versionAsInProject(),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").versionAsInProject(),
                mavenBundle().artifactId("guava").groupId("com.google.guava").versionAsInProject(),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").versionAsInProject(),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").versionAsInProject(),
                mavenBundle().artifactId("jca-lib").groupId("edu.gemini.aspen").update().versionAsInProject(),
                mavenBundle().artifactId("epics-service").groupId("edu.gemini.epics").update().versionAsInProject()
        );
    }

    @Test
    public void epicsServiceBundleStart() {
        assertNotNull(getEpicsServiceBundle());
        assertEquals(Bundle.ACTIVE, getEpicsServiceBundle().getState());
    }

    @Test
    public void epicsServiceHasStarted() {
        ServiceReference reference = context.getServiceReference(JCAContextController.class.getName());
        assertNotNull(reference);
    }

    @Test
    public void registerMockEpicsClient() throws InterruptedException {
        EpicsClientMock epicsClient = new EpicsClientMock();

        Dictionary<String, String[]> serviceProperties = new Hashtable<String, String[]>();
        serviceProperties.put(EpicsClient.EPICS_CHANNELS, new String[] {"ws:wsFilter.VALM"});
        ServiceRegistration serviceRegistration = context.registerService(EpicsClient.class.getName(), epicsClient, serviceProperties);

        // Give it 3 seconds
        TimeUnit.MILLISECONDS.sleep(3000);
        assertTrue(epicsClient.wasConnectedCalled());
        assertFalse(epicsClient.wasDisconnectedCalled());
        assertTrue(epicsClient.getUpdatesCount() > 0);

        serviceRegistration.unregister();
        assertTrue(epicsClient.wasDisconnectedCalled());
    }

    @Test
    public void registerMockEpicsClientWithNoProperties() throws InterruptedException {
        EpicsClientMock epicsClient = new EpicsClientMock();

        Dictionary<String, String[]> serviceProperties = new Hashtable<String, String[]>();
        ServiceRegistration serviceRegistration = context.registerService(EpicsClient.class.getName(), epicsClient, serviceProperties);

        // Wait a little bit
        TimeUnit.MILLISECONDS.sleep(500);
        assertFalse(epicsClient.wasConnectedCalled());

        serviceRegistration.unregister();
    }

    private Bundle getEpicsServiceBundle() {
        for (Bundle b : context.getBundles()) {
            if ("edu.gemini.epics.service".equals(b.getSymbolicName())) {
                return b;
            }
        }
        fail("Bundle not found");
        throw new AssertionError();
    }

}
