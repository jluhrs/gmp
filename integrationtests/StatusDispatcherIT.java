package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.giapi.status.dispatcher.FilteredStatusHandler;
import edu.gemini.aspen.giapi.status.dispatcher.StatusDispatcher;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.setter.StatusSetterImpl;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregate;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.WrappedUrlProvisionOption;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
public class StatusDispatcherIT extends FelixContainerConfigurationBase {

    @Inject
    private BundleContext context;

    @Configuration
    public static Option[] withStatusDbAndJMSProviderConfig() {
        return options(
                mavenBundle().artifactId("giapi").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("jms-api").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("giapi-jms-util").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("gmp-statusdb").groupId("edu.gemini.aspen.gmp").versionAsInProject(),
                mavenBundle().artifactId("jms-activemq-provider").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("jms-activemq-broker").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("eproperties").groupId("edu.gemini.external.osgi.net.jmatrix.eproperties").versionAsInProject(),
                mavenBundle().artifactId("activemq-core").groupId("org.apache.activemq").versionAsInProject(),
                mavenBundle().artifactId("geronimo-j2ee-management_1.1_spec").groupId("org.apache.geronimo.specs").versionAsInProject(),
                mavenBundle().artifactId("kahadb").groupId("org.apache.activemq").versionAsInProject(),
                mavenBundle().artifactId("geronimo-annotation_1.0_spec").groupId("org.apache.geronimo.specs").versionAsInProject(),
                mavenBundle().artifactId("giapi-status-service").groupId("edu.gemini.aspen").update().versionAsInProject(),
                mavenBundle().artifactId("joda-time").groupId("joda-time").update().versionAsInProject(),
                mavenBundle().artifactId("giapi-status-dispatcher").groupId("edu.gemini.aspen").update().versionAsInProject(),
                mavenBundle().artifactId("gmp-status-gateway").groupId("edu.gemini.aspen.gmp").update().versionAsInProject(),
                mavenBundle().artifactId("gmp-heartbeat").groupId("edu.gemini.aspen.gmp").update().versionAsInProject(),
                mavenBundle().artifactId("gmp-heartbeat-distributor-service").groupId("edu.gemini.aspen").update().versionAsInProject(),
                wrappedBundle(maven().artifactId("giapi-test-support").groupId("edu.gemini.aspen").versionAsInProject()).overwriteManifest(WrappedUrlProvisionOption.OverwriteMode.FULL),
                wrappedBundle(maven().artifactId("integration-tests").groupId("edu.gemini.aspen").versionAsInProject()).overwriteManifest(WrappedUrlProvisionOption.OverwriteMode.FULL),
                mavenBundle().artifactId("gmp-top").groupId("edu.gemini.gmp").update().versionAsInProject()
        );
    }

    @Override
    protected String confDir() {
        return "/src/test/resources/conf/services";
    }

    //@Test
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
    public void checkBinding() throws Exception {
        //register handlers
        TestHandler testHandler1 = new TestHandler();
        context.registerService(FilteredStatusHandler.class.getName(), testHandler1, null);
        TestHandler testHandler2 = new TestHandler();
        context.registerService(FilteredStatusHandler.class.getName(), testHandler2, null);
        JmsProvider provider = (JmsProvider) context.getService(context.getServiceReference("edu.gemini.jms.api.JmsProvider"));
        assertNotNull(provider);
        assertNotNull(context.getService(context.getServiceReference(StatusHandlerAggregate.class.getName())));

        // Wait a bit for the services to be registered before sending the status update
        TimeUnit.MILLISECONDS.sleep(200);

        //send StatusItem update via JMS
        StatusSetterImpl ss = new StatusSetterImpl("Test Status Setter", "gpi:status1");
        ss.startJms(provider);
        ss.setStatusItem(new BasicStatus<String>("gpi:status1", "gpi:status1"));

        //wait for messages to arrive and assert
        assertTrue(testHandler1.waitOnLatch(1, TimeUnit.SECONDS));
        assertTrue(testHandler2.waitOnLatch(1, TimeUnit.SECONDS));
        Assert.assertEquals(1, testHandler1.getCounter());
        Assert.assertEquals(1, testHandler2.getCounter());

        ss.stopJms();

    }

}
