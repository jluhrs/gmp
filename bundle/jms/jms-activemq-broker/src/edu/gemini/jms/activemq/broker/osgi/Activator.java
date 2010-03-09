package edu.gemini.jms.activemq.broker.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.logging.Logger;

import edu.gemini.jms.api.Broker;
import edu.gemini.jms.activemq.broker.ActiveMQBroker;

/**
 * Activator for the JMS provider based on Apache Active MQ
 */
public class Activator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());

    private Broker _broker = null;

    private static final String BROKER_URL_PROP           = "edu.gemini.jms.activemq.broker.url";
    private static final String BROKER_NAME               = "edu.gemini.jms.activemq.broker.name";
    private static final String BROKER_USE_JMX            = "edu.gemini.jms.activemq.broker.jmx";
    private static final String BROKER_IS_PERSISTENT      = "edu.gemini.jms.activemq.broker.persistent";
    private static final String BROKER_DELETE_MSG_STARTUP = "edu.gemini.jms.activemq.broker.deleteonstartup";

    public void start(BundleContext bundleContext) throws Exception {
        LOG.info("Starting ActiveMQ JMS Broker");

        ActiveMQBroker.Builder builder = new ActiveMQBroker.Builder();

        String brokerUrl = bundleContext.getProperty(BROKER_URL_PROP);
        if (brokerUrl != null) {
            builder.url(brokerUrl);
        }
        String brokerName = bundleContext.getProperty(BROKER_NAME);
        if (brokerName != null) {
            builder.name(brokerName);
        }
        String useJmx = bundleContext.getProperty(BROKER_USE_JMX);
        if (useJmx != null) {
            builder.useJmx(getBoolean(useJmx));
        }
        String isPersistent = bundleContext.getProperty(BROKER_IS_PERSISTENT);
        if (isPersistent != null) {
            builder.persistent(getBoolean(isPersistent));
        }
        String deleteOnStartup = bundleContext.getProperty(BROKER_DELETE_MSG_STARTUP);
        if (deleteOnStartup != null) {
            builder.deleteMsgOnStartup(getBoolean(deleteOnStartup));
        }
        _broker = builder.build();
        _broker.start();
    }

    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stopping ActiveMQ JMS Broker");
        _broker.shutdown();
        _broker = null;
    }

    //auxiliary method to convert string value to a boolean
    private boolean getBoolean(String s) {
        return s != null && s.trim().equalsIgnoreCase("true");
    }
}
