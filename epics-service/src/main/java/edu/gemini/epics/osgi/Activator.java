package edu.gemini.epics.osgi;

import edu.gemini.epics.EpicsService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private EpicsServiceFactory factory;
    private ServiceRegistration<ManagedServiceFactory> factoryService;

    @Override
    public void start(BundleContext context) {
        Hashtable<String, String> props = new Hashtable<>();
        props.put("service.pid", EpicsService.class.getName());

        factory = new EpicsServiceFactory(context);

        factoryService = context.registerService(ManagedServiceFactory.class, factory, props);
    }

    @Override
    public void stop(BundleContext context) {
        if (factory != null) {
            factory.stopServices();
            factory = null;
        }
        if (factoryService != null) {
            factoryService.unregister();
            factoryService = null;
        }
    }
}
