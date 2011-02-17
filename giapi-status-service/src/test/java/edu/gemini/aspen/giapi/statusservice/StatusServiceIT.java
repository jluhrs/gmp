package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.AppliesTo;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

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
    public static Option[] baseConfig() {
        return options(
                felix(),
                mavenBundle().artifactId("org.apache.felix.ipojo").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("com.springsource.javax.jms").groupId("javax.jms").versionAsInProject(),
                mavenBundle().artifactId("org.osgi.compendium").groupId("org.osgi").version("4.2.0"),
                mavenBundle().artifactId("giapi").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("jms-api").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("giapi-jms-util").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("giapi-status-service").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("guava").groupId("com.google.guava").version("8.0.0")
        );
    }

    @Configuration
    @AppliesTo({"withStatusDb.*"})
    public static Option[] withStatusDbConfig() {
        // Add status-db to have a StatusHandler configured
        return options(
                mavenBundle().artifactId("gmp-statusdb").groupId("edu.gemini.aspen.gmp").version("0.1.0")
        );
    }

    @Configuration
    @AppliesTo({"withStatusDbAndJMSProvider.*"})
    public static Option[] withStatusDbAndJMSProviderConfig() {
        // Add JMS Provider
        return options(
                // Properties required to ensure the activemq broker and provider don't collide
                systemProperty("edu.gemini.jms.activemq.broker.url").value("vm://gmp"),
                systemProperty("edu.gemini.jms.activemq.provider.url").value("vm://gmp"),
                //systemProperty("ipojo.log.level").value("debug"),
                systemProperty("log4j.configuration").value("/Users/cquiroz/log4j.properties"),

                mavenBundle().artifactId("gmp-statusdb").groupId("edu.gemini.aspen.gmp").version("0.1.0"),
                mavenBundle().artifactId("jms-activemq-provider").groupId("edu.gemini.jms").version("1.1.0"),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").version("1.5.3"),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").version("1.5.3"),
                mavenBundle().artifactId("jms-activemq-broker").groupId("edu.gemini.jms").version("1.1.0"),
                mavenBundle().artifactId("activemq-core").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-j2ee-management_1.1_spec").groupId("org.apache.geronimo.specs").version("1.0.1"),
                mavenBundle().artifactId("kahadb").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-annotation_1.0_spec").groupId("org.apache.geronimo.specs").version("1.1.1"),
                mavenBundle().artifactId("com.springsource.org.apache.commons.logging").groupId("org.apache.commons").version("1.1.1")
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

    @Test
    public void noDependantServices() {
        assertTrue("StatusService should exist as a managed service", isStatusServiceRunning());
        assertFalse("StatusHandlerAggregate should not exist as dependencies are not met", isStatusHandlerAggregateRunning());
        assertFalse("StatusHandlers are not available", isStatusHandlerInUse());
        assertFalse("JMSProvider are not available", isJMSProviderInUse());
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

    @Test
    public void withStatusDbOneStatusServiceShouldNotExist() {
        assertTrue("StatusService should exist as a managed service", isStatusServiceRunning());
        assertTrue("StatusHandlerAggregate should exist as dependencies are met", isStatusHandlerAggregateRunning());
        assertTrue("StatusHandlers are not in use", isStatusHandlerInUse());
        assertFalse("JMSProvider are not available", isJMSProviderInUse());
    }

}
