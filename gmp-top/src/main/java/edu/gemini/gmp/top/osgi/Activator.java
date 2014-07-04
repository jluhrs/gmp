package edu.gemini.gmp.top.osgi;

import edu.gemini.gmp.top.Top;
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
        props.put("service.pid", Top.class.getName());

        TopImplFactory providerFactory = new TopImplFactory(context);

        factoryService = context.registerService(ManagedServiceFactory.class, providerFactory, props);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if (factoryService != null) {
            factoryService.unregister();
            factoryService = null;
        }
    }
}
