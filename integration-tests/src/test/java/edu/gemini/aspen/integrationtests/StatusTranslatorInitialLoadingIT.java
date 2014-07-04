package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.dispatcher.FilteredStatusHandler;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.setter.StatusSetterImpl;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregate;
import edu.gemini.gmp.status.translator.JmsStatusItemTranslatorImpl;
import edu.gemini.gmp.status.translator.LocalStatusItemTranslator;
import edu.gemini.gmp.status.translator.StatusItemTranslator;
import edu.gemini.gmp.top.Top;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.WrappedUrlProvisionOption;
import org.osgi.framework.BundleContext;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
public class StatusTranslatorInitialLoadingIT extends FelixContainerConfigurationBase {
    private static final Logger LOG = Logger.getLogger(StatusTranslatorInitialLoadingIT.class.getName());

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
                mavenBundle().artifactId("gmp-top").groupId("edu.gemini.aspen.gmp").update().versionAsInProject(),
                mavenBundle().artifactId("giapi-status-service").groupId("edu.gemini.aspen").update().versionAsInProject(),
                mavenBundle().artifactId("joda-time").groupId("joda-time").update().versionAsInProject(),
                mavenBundle().artifactId("giapi-status-dispatcher").groupId("edu.gemini.aspen").update().versionAsInProject(),
                mavenBundle().artifactId("gmp-status-gateway").groupId("edu.gemini.aspen.gmp").update().versionAsInProject(),
                mavenBundle().artifactId("gmp-heartbeat").groupId("edu.gemini.aspen.gmp").update().versionAsInProject(),
                mavenBundle().artifactId("gmp-heartbeat-distributor-service").groupId("edu.gemini.aspen").update().versionAsInProject(),
                wrappedBundle(maven().artifactId("giapi-test-support").groupId("edu.gemini.aspen").versionAsInProject()).overwriteManifest(WrappedUrlProvisionOption.OverwriteMode.FULL),
                wrappedBundle(maven().artifactId("integration-tests").groupId("edu.gemini.aspen").versionAsInProject()).overwriteManifest(WrappedUrlProvisionOption.OverwriteMode.FULL)

        );
    }

    @Override
    protected String confDir() {
        return "/src/test/resources/conf/status-item-translator/initial";
    }


    @Test
    public void localTranslatorInitialLoading() throws Exception {
        JmsProvider provider = (JmsProvider) context.getService(context.getServiceReference("edu.gemini.jms.api.JmsProvider"));
        assertNotNull(provider);
        assertNotNull(context.getService(context.getServiceReference(StatusHandlerAggregate.class.getName())));

        Top top = (Top)context.getService(context.getServiceReference(Top.class.getName()));
        assertNotNull(top);
        StatusHandlerAggregate aggregate = (StatusHandlerAggregate)context.getService(context.getServiceReference(StatusHandlerAggregate.class.getName()));
        assertNotNull(aggregate);



        // Wait a bit for the services to be registered before sending the status update
        TimeUnit.MILLISECONDS.sleep(1000);

        LOG.info("Sending update");
        //send StatusItem update via JMS
        StatusSetterImpl ss = new StatusSetterImpl("Test Status Setter", "gpisim:old");
        ss.startJms(provider);
        ss.setStatusItem(new BasicStatus<Integer>("gpisim:old", 0));
        TimeUnit.MILLISECONDS.sleep(1000);
        LOG.info("Update sent");

        //register handlers
        StatusTranslatorTestHandler testHandler1 = new StatusTranslatorTestHandler();
        context.registerService(FilteredStatusHandler.class.getName(), testHandler1, null);
        TimeUnit.MILLISECONDS.sleep(1000);

        LOG.info("Starting translator");
        LocalStatusItemTranslator translator =   new LocalStatusItemTranslator(top, aggregate, "${felix.fileinstall.dir}/../../status-translator.xml");
        context.registerService(new String[]{JmsArtifact.class.getName(), StatusItemTranslator.class.getName()},translator,null);

        TimeUnit.MILLISECONDS.sleep(1000);
        translator.start();
        TimeUnit.MILLISECONDS.sleep(1000);
        LOG.info("Translator started");

        //wait for messages to arrive and assert
        assertTrue(testHandler1.waitOnLatch(1, TimeUnit.SECONDS));
        Assert.assertEquals(1, testHandler1.getCounter());
        Assert.assertEquals(Health.GOOD, testHandler1.getValue());

        ss.stopJms();

    }

    @Test
    public void jmsTranslatorInitialLoading() throws Exception {
        JmsProvider provider = (JmsProvider) context.getService(context.getServiceReference("edu.gemini.jms.api.JmsProvider"));
        assertNotNull(provider);
        assertNotNull(context.getService(context.getServiceReference(StatusHandlerAggregate.class.getName())));

        Top top = (Top)context.getService(context.getServiceReference(Top.class.getName()));
        assertNotNull(top);

        // Wait a bit for the services to be registered before sending the status update
        TimeUnit.MILLISECONDS.sleep(1000);

        LOG.info("Sending update");
        //send StatusItem update via JMS
        StatusSetterImpl ss = new StatusSetterImpl("Test Status Setter", "gpisim:old");
        ss.startJms(provider);
        ss.setStatusItem(new BasicStatus<Integer>("gpisim:old", 0));
        TimeUnit.MILLISECONDS.sleep(1000);
        LOG.info("Update sent");

        //register handlers
        StatusTranslatorTestHandler testHandler1 = new StatusTranslatorTestHandler();
        context.registerService(FilteredStatusHandler.class.getName(), testHandler1, null);
        TimeUnit.MILLISECONDS.sleep(1000);

        LOG.info("Starting translator");
        JmsStatusItemTranslatorImpl translator =   new JmsStatusItemTranslatorImpl(top, "${felix.fileinstall.dir}/../../status-translator.xml");
        context.registerService(new String[]{JmsArtifact.class.getName(), StatusItemTranslator.class.getName()},translator,null);

        TimeUnit.MILLISECONDS.sleep(1000);
        translator.start();
        TimeUnit.MILLISECONDS.sleep(1000);
        LOG.info("Translator started");

        //wait for messages to arrive and assert
        assertTrue(testHandler1.waitOnLatch(1, TimeUnit.SECONDS));
        Assert.assertEquals(1, testHandler1.getCounter());
        Assert.assertEquals(Health.GOOD, testHandler1.getValue());

        ss.stopJms();

    }
}
