package edu.gemini.jms.activemq.broker;

/**
 * Integration test for ActiveMQBroker. it attempts to launch and configure an ActiveMQBroker
 */

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

import javax.jms.Connection;
import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.cleanCaches;

@RunWith(JUnit4TestRunner.class)
public class ActiveMQBrokerIT {
    @Inject
    private BundleContext context;

    @Configuration
    public static Option[] baseConfig() {
        return options(
                felix(),
                waitForFrameworkStartup(),
                cleanCaches(),
                systemProperty("felix.fileinstall.dir").value(System.getProperty("basedir") + "/src/test/resources/conf/services"),
                systemProperty("felix.fileinstall.noInitialDelay").value("true"),
                mavenBundle().artifactId("org.apache.felix.ipojo").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("com.springsource.javax.jms").groupId("javax.jms").versionAsInProject(),
                mavenBundle().artifactId("jms-api").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("org.osgi.compendium").groupId("org.osgi").version("4.2.0"),
                mavenBundle().artifactId("org.apache.felix.configadmin").groupId("org.apache.felix").version("1.2.8"),
                mavenBundle().artifactId("org.apache.felix.fileinstall").groupId("org.apache.felix").version("3.1.10"),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("activemq-core").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-j2ee-management_1.1_spec").groupId("org.apache.geronimo.specs").version("1.0.1"),
                mavenBundle().artifactId("kahadb").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-annotation_1.0_spec").groupId("org.apache.geronimo.specs").version("1.1.1"),
                mavenBundle().artifactId("com.springsource.org.apache.commons.logging").groupId("org.apache.commons").version("1.1.1"),
                mavenBundle().artifactId("guava").groupId("com.google.guava").versionAsInProject(),
                mavenBundle().artifactId("jms-activemq-broker").groupId("edu.gemini.jms").version("1.1.0").update().startLevel(6));
    }

    @Test
    public void testBundleStarted() throws BundleException, InterruptedException, JMSException {
        assertNotNull(getJmsActiveMQBrokerBundle());
        assertEquals(getJmsActiveMQBrokerBundle().getState(), Bundle.ACTIVE);
    }

    @Test
    public void testBrokerIsRunning() throws BundleException, InterruptedException, JMSException {
        // The parameters of the connection are very relevant:
        // create=false prevents another broker to be created by default
        // waitForStart=4000 ensure the connection will wait for the broker to be started before giving up
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://gmp?create=false&waitForStart=4000");
        Connection connection = connectionFactory.createConnection();
        assertEquals("5.4.2", connection.getMetaData().getProviderVersion());
    }

    private Bundle getJmsActiveMQBrokerBundle() throws BundleException {
        for (Bundle b : context.getBundles()) {
            if ("edu.gemini.jms.activemq-broker".equals(b.getSymbolicName())) {
                return b;
            }
        }
        return null;
    }
}
