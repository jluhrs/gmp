package edu.gemini.jms.activemq.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.logging.Logger;

import edu.gemini.jms.api.Broker;
import edu.gemini.jms.activemq.broker.ActiveMQBroker;

/**
 * Activator for the JMS provider based on Apache Active MQ
 */
public class Activator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());

    private Broker _broker = null;

    private ServiceRegistration _registration;

    public void start(BundleContext bundleContext) throws Exception {
        LOG.info("Starting ActiveMQ JMS Provider");
        _broker = new ActiveMQBroker();
        _broker.start();

        //advertise the ActiveMQ service in the OSGi framework
        _registration = bundleContext.registerService(
                Broker.class.getName(),
                _broker, null);

    }

    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stopping ActiveMQ JMS Provider");
        _broker.shutdown();
        _broker = null;
        //notify the OSGi framework this service is not longer available
        _registration.unregister();
    }
}
