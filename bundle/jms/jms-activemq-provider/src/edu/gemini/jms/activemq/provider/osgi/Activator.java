package edu.gemini.jms.activemq.provider.osgi;

import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 */
public class Activator implements BundleActivator {

    private ServiceRegistration _registration;

    private JmsProvider _provider = null;
    
    public void start(BundleContext bundleContext) throws Exception {
        _provider = new ActiveMQJmsProvider();
        //advertise the ActiveMQ provider in the OSGi framework
        _registration = bundleContext.registerService(
                JmsProvider.class.getName(),
                _provider, null);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        _provider = null;
        //notify the OSGi framework this service is not longer available
        _registration.unregister();
    }
}
