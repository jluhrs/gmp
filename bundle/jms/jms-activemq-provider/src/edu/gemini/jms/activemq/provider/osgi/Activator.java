package edu.gemini.jms.activemq.provider.osgi;

import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.logging.Logger;

/**
 * Activator class for the JMS Provided based on ActiveMQ
 */
public class Activator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());
    private static final String BROKER_URL_PROP = "edu.gemini.jms.activemq.provider.url";
    private ServiceRegistration _registration;

    private JmsProvider _provider = null;
    
    public void start(BundleContext bundleContext) throws Exception {

        String brokerUrl = bundleContext.getProperty(BROKER_URL_PROP);

        if (brokerUrl != null) {
            _provider = new ActiveMQJmsProvider(brokerUrl);
        } else {
            brokerUrl = "default";
            _provider = new ActiveMQJmsProvider();
        }
        //advertise the ActiveMQ provider in the OSGi framework
        _registration = bundleContext.registerService(
                JmsProvider.class.getName(),
                _provider, null);
        LOG.info("ActiveMQ JMS Provider initialized (" + brokerUrl + ")");
    }

    public void stop(BundleContext bundleContext) throws Exception {
        _provider = null;
        //notify the OSGi framework this service is not longer available
        _registration.unregister();
        LOG.info("ActiveMQ JMS Provider destroyed");
    }
}
