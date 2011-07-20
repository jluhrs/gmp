package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregate;
import edu.gemini.aspen.giapi.statusservice.StatusService;
import edu.gemini.jms.api.*;
import org.junit.*;
import org.junit.runner.*;
import org.ops4j.pax.exam.*;
import org.ops4j.pax.exam.junit.*;
import org.osgi.framework.*;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.*;

/**
 * Basic integration test for StatusService basically verifying that the services run or do not run depending
 * on the other related dependencies
 *
 * For testing it uses pax-exam to create a felix based run time. Different set of bundles are used for
 * different test according to the filter set with the @AppliesTo annotation as in
 *
 * <a href="http://paxrunner.ops4j.org/display/paxexam/Advanced+JUnit+usage">Pax-examn</a>
 *
 * @cquiroz
 */
@RunWith(JUnit4TestRunner.class)
public class StatusServiceIT {
    @Inject
    private BundleContext context;

    @Configuration
    public static Option[] withStatusDbAndJMSProviderConfig() {
        return options(
                felix(),
                cleanCaches(),
                systemProperty("felix.fileinstall.dir").value(System.getProperty("basedir") + "/src/test/resources/conf/services"),
                systemProperty("felix.fileinstall.noInitialDelay").value("true"),
                mavenBundle().artifactId("org.apache.felix.ipojo").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("com.springsource.javax.jms").groupId("javax.jms").versionAsInProject(),
                mavenBundle().artifactId("org.osgi.compendium").groupId("org.osgi").version("4.2.0"),
                mavenBundle().artifactId("giapi").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("jms-api").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("giapi-jms-util").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("gmp-statusdb").groupId("edu.gemini.aspen.gmp").version("0.1.0"),
                mavenBundle().artifactId("org.apache.felix.configadmin").groupId("org.apache.felix").version("1.2.8"),
                mavenBundle().artifactId("org.apache.felix.fileinstall").groupId("org.apache.felix").version("3.1.10"),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("guava").groupId("com.google.guava").version("8.0.0"),
                mavenBundle().artifactId("gmp-statusdb").groupId("edu.gemini.aspen.gmp").version("0.1.0"),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("jms-activemq-provider").groupId("edu.gemini.jms").version("1.1.0"),
                mavenBundle().artifactId("jms-activemq-broker").groupId("edu.gemini.jms").version("1.1.0"),
                mavenBundle().artifactId("activemq-core").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-j2ee-management_1.1_spec").groupId("org.apache.geronimo.specs").version("1.0.1"),
                mavenBundle().artifactId("kahadb").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-annotation_1.0_spec").groupId("org.apache.geronimo.specs").version("1.1.1"),
                mavenBundle().artifactId("com.springsource.org.apache.commons.logging").groupId("org.apache.commons").version("1.1.1"),
                mavenBundle().artifactId("giapi-status-service").groupId("edu.gemini.aspen").update().versionAsInProject()
                );
    }

    @Test
    public void bundleExistence() {
        assertNotNull(getStatusServiceBundle());
    }

    private Bundle getStatusServiceBundle() {
        for (Bundle b : context.getBundles()) {
            if ("edu.gemini.aspen.giapi-status-service".equals(b.getSymbolicName())) {
                return b;
            }
        }
        fail("Bundle not found");
        throw new AssertionError();
    }

    private boolean isStatusServiceRunning() {
        Bundle statusServiceBundle = getStatusServiceBundle();
        for (ServiceReference s : statusServiceBundle.getRegisteredServices()) {
            if (StatusService.class.getName().equals(s.getProperty("service.pid"))) {
                return true;
            }
        }
        return false;
    }

    private boolean isStatusHandlerAggregateRunning() {
        return isServiceClassInUse(StatusHandlerAggregate.class);
    }

    private boolean isServiceClassInUse(Class<?> serviceClass) {
        ServiceReference aggregateReference = context.getServiceReference(serviceClass.getName());
        return aggregateReference != null;
    }

    private boolean isStatusHandlerInUse() {
        return isServiceClassInUse(StatusHandler.class);
    }

    private boolean isJMSProviderInUse() {
        return isServiceClassInUse(JmsProvider.class);
    }

    @Test
    public void withStatusDbAndJMSProviderStatusServiceShouldExist() {
        assertTrue("StatusService should exist as a managed service", isStatusServiceRunning());
        assertTrue("StatusHandlerAggregate should exist as dependencies are met", isStatusHandlerAggregateRunning());
        assertTrue("StatusDB should be available", isStatusHandlerInUse());
        assertTrue("JMSProvider should be available", isJMSProviderInUse());
    }

}
