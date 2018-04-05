package edu.gemini.aspen.integrationtests;

/**
 * Integration test for ActiveMQBroker. it attempts to launch and configure an ActiveMQBroker
 */

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ActiveMQBrokerIT extends FelixContainerConfigurationBase {
    @Inject
    private BundleContext context;

    @Configuration
    public Option[] baseConfig() {
        return concatenate(super.baseContainerConfig(), options(
                mavenBundle().artifactId("jms-api").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("kahadb").groupId("org.apache.activemq").versionAsInProject(),
                mavenBundle().artifactId("geronimo-j2ee-management_1.1_spec").groupId("org.apache.geronimo.specs").versionAsInProject(),
                mavenBundle().artifactId("geronimo-annotation_1.0_spec").groupId("org.apache.geronimo.specs").versionAsInProject(),
                mavenBundle().artifactId("activemq-core").groupId("org.apache.activemq").versionAsInProject(),
                mavenBundle().artifactId("jms-activemq-broker").groupId("edu.gemini.jms").versionAsInProject()
        ));
    }

    @Test
    public void testBundleStarted() {
        assertNotNull(getJmsActiveMQBrokerBundle());
        assertEquals(getJmsActiveMQBrokerBundle().getState(), Bundle.ACTIVE);
    }

    @Test
    public void testBrokerIsRunning() throws JMSException {
        // The parameters of the connection are very relevant:
        // create=false prevents another broker to be created by default
        // waitForStart=4000 ensure the connection will wait for the broker to be started before giving up
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://gmp?create=false&waitForStart=4000");
        Connection connection = connectionFactory.createConnection();
        assertEquals("5.5.1", connection.getMetaData().getProviderVersion());
    }

    private Bundle getJmsActiveMQBrokerBundle() {
        for (Bundle b : context.getBundles()) {
            if ("edu.gemini.jms.activemq-broker".equals(b.getSymbolicName())) {
                return b;
            }
        }
        return null;
    }

    @Override
    protected String confDir() {
        return "/src/test/resources/conf/jms-activemq-broker";
    }
}
