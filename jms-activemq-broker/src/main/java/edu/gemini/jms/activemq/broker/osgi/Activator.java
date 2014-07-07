package edu.gemini.jms.activemq.broker.osgi;

import edu.gemini.jms.activemq.broker.ActiveMQBrokerComponent;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Hashtable;

public class Activator implements BundleActivator {
    private ServiceRegistration<ManagedServiceFactory> factoryService;

    @Override
    public void start(BundleContext context) throws Exception {
        Hashtable<String, String> props = new Hashtable<String, String>();
        props.put("service.pid", ActiveMQBrokerComponent.class.getName());

        ActiveMQBrokerFactory providerFactory = new ActiveMQBrokerFactory();

        factoryService = context.registerService(ManagedServiceFactory.class, providerFactory, props);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (factoryService != null) {
            factoryService.unregister();
            factoryService = null;
        }
    }
}
