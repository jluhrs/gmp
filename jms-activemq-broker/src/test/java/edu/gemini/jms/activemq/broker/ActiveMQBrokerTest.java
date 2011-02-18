package edu.gemini.jms.activemq.broker;

/**
 * Integration test for ActiveMQBroker. it attempts to launch and configure an ActiveMQBroker
 */

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
@Ignore
public class ActiveMQBrokerTest {
    @Inject
    private BundleContext context;

    //@Before
    public void before() throws IOException {
        ConfigurationAdmin configAdmin = (ConfigurationAdmin) context.getService(context.getServiceReference(ConfigurationAdmin.class.getName()));
        org.osgi.service.cm.Configuration configuration = configAdmin.getConfiguration("edu.gemini.jms.activemq.broker.ActiveMQBroker");
//        configuration.getProperties()
        Dictionary dictionary = new Hashtable<String, String>();
        dictionary.put("brokerName", "gmp");
        dictionary.put("brokerUrl", "vm://gmp");
        dictionary.put("persistent", "false");
        dictionary.put("deleteMsgOnStartup", "true");
        dictionary.put("useAdvisoryMessages", "true");
        dictionary.put("useJmx", "false");
        dictionary.put("jmxRmiServerPort", "9000");
        dictionary.put("jmxConnectorPort", "1099");
        configuration.update(dictionary);
        System.out.println("UPDATE " + dictionary);
    }

    @Configuration
    public static Option[] baseConfig() {
        //System.out.println(System.getProperties());
        //getClass().getResource("")
        return options(

                felix(),
                waitForFrameworkStartup(),
                //systemProperty("ipojo.log.level").value("debug"),
                systemProperty("felix.cm.loglevel").value("4"),
                systemProperty("felix.fileinstall.log.level").value("debug"),
                systemProperty("felix.fileinstall.dir").value(System.getProperty("basedir") + "/src/test/resources/conf"),
                systemProperty("felix.fileinstall.noInitialDelay").value("true"),
//                  <felix.fileinstall.log.level>4</felix.fileinstall.log.level>
//        <felix.fileinstall.noInitialDelay>true</felix.fileinstall.noInitialDelay>
                //systemProperty("felix.cm.loglevel").value("4"),
//        <felix.fileinstall.dir>${conf.base}/services</felix.fileinstall.dir>
                provision(
                mavenBundle().artifactId("org.apache.felix.ipojo").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("com.springsource.javax.jms").groupId("javax.jms").versionAsInProject(),
                mavenBundle().artifactId("jms-api").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("org.osgi.compendium").groupId("org.osgi").version("4.2.0"),
                // mavenBundle().artifactId("giapi-jms-util").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("org.apache.felix.configadmin").groupId("org.apache.felix").version("1.2.8"),
                mavenBundle().artifactId("org.apache.felix.fileinstall").groupId("org.apache.felix").version("3.1.10"),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").version("1.5.3"),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").version("1.5.3"),
                mavenBundle().artifactId("activemq-core").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-j2ee-management_1.1_spec").groupId("org.apache.geronimo.specs").version("1.0.1"),
                mavenBundle().artifactId("kahadb").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-annotation_1.0_spec").groupId("org.apache.geronimo.specs").version("1.1.1"),
                mavenBundle().artifactId("com.springsource.org.apache.commons.logging").groupId("org.apache.commons").version("1.1.1")),

                mavenBundle().artifactId("jms-activemq-broker").groupId("edu.gemini.jms").version("1.1.0").update().startLevel(6));
    }

    @Test
    public void testBrokerRunning() throws BundleException, InterruptedException {
        System.out.println("STAAAAAT");
        TimeUnit.MILLISECONDS.sleep(2200L);
        getStatusServiceBundle();
        System.out.println("61616");

    }

    private Bundle getStatusServiceBundle() throws BundleException {
        for (Bundle b : context.getBundles()) {
            if ("edu.gemini.jms.activemq-broker".equals(b.getSymbolicName())) {
                b.start();
            }
        }
        return null;
    }
}
