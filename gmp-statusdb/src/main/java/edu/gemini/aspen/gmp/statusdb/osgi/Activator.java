package edu.gemini.aspen.gmp.statusdb.osgi;

import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.gmp.statusdb.StatusDatabase;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private ServiceRegistration<?> serviceRegistration;

    @Override
    public void start(BundleContext context) throws Exception {
        serviceRegistration = context.registerService(new String[] {StatusDatabaseService.class.getName(), StatusHandler.class.getName()}, new StatusDatabase(), new Hashtable<String, String>());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            serviceRegistration = null;
        }

    }
}
