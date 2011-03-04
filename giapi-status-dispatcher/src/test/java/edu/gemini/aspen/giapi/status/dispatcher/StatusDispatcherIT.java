package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapitestsupport.StatusSetter;
import edu.gemini.jms.api.JmsProvider;
import org.junit.*;
import org.junit.runner.*;
import org.ops4j.pax.exam.*;
import org.ops4j.pax.exam.junit.*;
import org.osgi.framework.*;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.*;

@RunWith(JUnit4TestRunner.class)
public class StatusDispatcherIT{

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
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("jms-activemq-provider").groupId("edu.gemini.jms").version("1.1.0"),
                mavenBundle().artifactId("jms-activemq-broker").groupId("edu.gemini.jms").version("1.1.0"),
                mavenBundle().artifactId("activemq-core").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-j2ee-management_1.1_spec").groupId("org.apache.geronimo.specs").version("1.0.1"),
                mavenBundle().artifactId("kahadb").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-annotation_1.0_spec").groupId("org.apache.geronimo.specs").version("1.1.1"),
                mavenBundle().artifactId("com.springsource.org.apache.commons.logging").groupId("org.apache.commons").version("1.1.1"),
                mavenBundle().artifactId("giapi-test-support").groupId("edu.gemini.aspen").update().versionAsInProject(),
                mavenBundle().artifactId("giapi-status-service").groupId("edu.gemini.aspen").update().versionAsInProject(),
                mavenBundle().artifactId("giapi-status-dispatcher").groupId("edu.gemini.aspen").update().versionAsInProject()
                );
    }

    @Test
    public void bundleExistence() {
        assertNotNull(getStatusDispatcherBundle());
        assertTrue(isStatusDispatcherRunning());
    }

    private Bundle getStatusDispatcherBundle() {
        for (Bundle b : context.getBundles()) {
            if ("edu.gemini.aspen.giapi-status-dispatcher".equals(b.getSymbolicName())) {
                return b;
            }
        }
        fail();
        return null;
    }

    private boolean isStatusDispatcherRunning() {
        Bundle statusDispatcherBundle = getStatusDispatcherBundle();
        for (ServiceReference s : statusDispatcherBundle.getRegisteredServices()) {
            if (StatusDispatcher.class.getName().equals(s.getProperty("service.pid"))) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void checkBinding() throws Exception{
        //register handlers
        TestHandler testHandler1 = new TestHandler();
        context.registerService(FilteredStatusHandler.class.getName(),testHandler1,null);
        TestHandler testHandler2 = new TestHandler();
        context.registerService(FilteredStatusHandler.class.getName(),testHandler2,null);
        JmsProvider provider = (JmsProvider) context.getService(context.getServiceReference("edu.gemini.jms.api.JmsProvider"));

        //send StatusItem update via JMS
        StatusSetter ss = new StatusSetter("gpi:status1");
        ss.startJms(provider);
        ss.setStatusItem(new BasicStatus<String>("gpi:status1", "gpi:status1"));

        //wait for messages to arrive and assert
        testHandler1.waitOnLatch(1, TimeUnit.SECONDS);
        testHandler2.waitOnLatch(1, TimeUnit.SECONDS);
        assertEquals(1, testHandler1.getCounter());
        assertEquals(1, testHandler2.getCounter());

        ss.stopJms();

    }

}
