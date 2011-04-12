package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.heartbeatdistributor.HeartbeatConsumer;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.cleanCaches;

/**
 * Class HeartbeatDistributorIT
 *
 * @author Nicolas A. Barriga
 *         Date: 3/10/11
 */
@RunWith(JUnit4TestRunner.class)
public class HeartbeatDistributorIT {
    private final Logger LOG = Logger.getLogger(HeartbeatDistributorIT.class.getName());
    @Inject
    private BundleContext context;

    @Configuration
    public static Option[] withJMSProviderConfig() {
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
                mavenBundle().artifactId("org.apache.felix.configadmin").groupId("org.apache.felix").version("1.2.8"),
                mavenBundle().artifactId("org.apache.felix.fileinstall").groupId("org.apache.felix").version("3.1.10"),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("guava").groupId("com.google.guava").version("8.0.0"),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("jms-activemq-provider").groupId("edu.gemini.jms").version("1.1.0"),
                mavenBundle().artifactId("jms-activemq-broker").groupId("edu.gemini.jms").version("1.1.0"),
                mavenBundle().artifactId("activemq-core").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-j2ee-management_1.1_spec").groupId("org.apache.geronimo.specs").version("1.0.1"),
                mavenBundle().artifactId("kahadb").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-annotation_1.0_spec").groupId("org.apache.geronimo.specs").version("1.1.1"),
                mavenBundle().artifactId("com.springsource.org.apache.commons.logging").groupId("org.apache.commons").version("1.1.1"),
                mavenBundle().artifactId("gmp-heartbeat").groupId("edu.gemini.aspen.gmp").update().versionAsInProject(),
                mavenBundle().artifactId("heartbeat-distributor-service").groupId("edu.gemini.aspen").update().versionAsInProject(),
                mavenBundle().artifactId("giapi-status-dispatcher").groupId("edu.gemini.aspen").update().versionAsInProject(),
                mavenBundle().artifactId("integration-tests").groupId("edu.gemini.aspen").update().versionAsInProject()
                );
    }

    @Test
    public void bundleExistence() {
        assertNotNull(getHeartbeatDistributorBundle());
    }

    private Bundle getHeartbeatDistributorBundle() {
        for (Bundle b : context.getBundles()) {
            if ("edu.gemini.aspen.heartbeat-distributor-service".equals(b.getSymbolicName())) {
                return b;
            }
        }
        fail();
        return null;
    }

    @Test
    public void checkNotification() throws Exception{
        //register handlers
        JmsProvider provider = (JmsProvider) context.getService(context.getServiceReference("edu.gemini.jms.api.JmsProvider"));

        TestConsumerComponent comp = new TestConsumerComponent(2);
        context.registerService(HeartbeatConsumer.class.getName(),comp,null);

         //wait at most 3 second for 2 beats to arrive
        comp.waitOnLatch(3, TimeUnit.SECONDS);
        assertTrue(comp.getLast()>0);
        assertTrue(comp.getCount()>=2);

    }
}
