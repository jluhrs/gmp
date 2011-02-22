package edu.gemini.jms.activemq.provider;

import edu.gemini.jms.api.JmsProvider;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.cleanCaches;

/**
 * Basic integration test for JMS Provider basically verifying that the service starts and can be configured

 * For testing it uses pax-exam to create a felix based OSGi container.
 *
 * <a href="http://paxrunner.ops4j.org/display/paxexam/Advanced+JUnit+usage">Pax-examn</a>
 *
 * @cquiroz
 */
@RunWith(JUnit4TestRunner.class)
public class ActiveMQJMSProviderIT {
    @Inject
    private BundleContext context;

    @Configuration
    public static Option[] baseConfig() {
        return options(
                felix(),
                cleanCaches(),
                systemProperty("felix.fileinstall.dir").value(System.getProperty("basedir") + "/src/test/resources/conf/services"),
                systemProperty("felix.fileinstall.noInitialDelay").value("true"),
                mavenBundle().artifactId("org.apache.felix.ipojo").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("com.springsource.javax.jms").groupId("javax.jms").versionAsInProject(),
                mavenBundle().artifactId("org.osgi.compendium").groupId("org.osgi").version("4.2.0"),
                mavenBundle().artifactId("activemq-core").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-j2ee-management_1.1_spec").groupId("org.apache.geronimo.specs").version("1.0.1"),
                mavenBundle().artifactId("kahadb").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-annotation_1.0_spec").groupId("org.apache.geronimo.specs").version("1.1.1"),
                mavenBundle().artifactId("com.springsource.org.apache.commons.logging").groupId("org.apache.commons").version("1.1.1"),
                mavenBundle().artifactId("jms-api").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("org.apache.felix.configadmin").groupId("org.apache.felix").version("1.2.8"),
                mavenBundle().artifactId("org.apache.felix.fileinstall").groupId("org.apache.felix").version("3.1.10"),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("jms-activemq-provider").groupId("edu.gemini.jms").version("1.1.0").update()
        );
    }

    @Test
    public void bundleExistence() {
        assertNotNull(getJmsProviderBundle());
    }

    private Bundle getJmsProviderBundle() {
        for (Bundle b : context.getBundles()) {
            if ("edu.gemini.jms.activemq-provider".equals(b.getSymbolicName())) {
                return b;
            }
        }
        fail("Bundle not found");
        throw new AssertionError();
    }

    @Test
    public void doesServiceExist() throws IOException, BundleException, InterruptedException {
        assertTrue(isJMSProviderAvailable());
        JmsProvider provider = findJmsProvider();
        // WARN extra dependency on ActiveMQ API
        assertEquals("failover:(tcp://localhost:51616)", ((ActiveMQConnectionFactory)provider.getConnectionFactory()).getBrokerURL());

    }

    private boolean isJMSProviderAvailable() {
        ServiceReference providerReference = context.getServiceReference(JmsProvider.class.getName());
        return providerReference != null;
    }

    private JmsProvider findJmsProvider() {
        ServiceReference providerReference = context.getServiceReference(JmsProvider.class.getName());
        return (JmsProvider)context.getService(providerReference);
    }

}
