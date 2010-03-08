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

    public void start(BundleContext bundleContext) throws Exception {
        LOG.info("Starting ActiveMQ JMS Broker");
        _broker = new ActiveMQBroker();
        _broker.start();
    }

    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stopping ActiveMQ JMS Broker");
        _broker.shutdown();
        _broker = null;
    }
}
