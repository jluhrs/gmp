package edu.gemini.aspen.gmp.statusdb.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import edu.gemini.aspen.gmp.statusdb.StatusDatabase;
import edu.gemini.aspen.gmp.status.api.StatusHandler;

/**
 *
 */
public class Activator implements BundleActivator {
    
    private StatusDatabase _db;

    private ServiceRegistration _registration;

    public void start(BundleContext bundleContext) throws Exception {
        _db = new StatusDatabase();
        _db.start();
        //advertise the status database (a Status Handler) in OSGi

        _registration = bundleContext.registerService(
                StatusHandler.class.getName(),
                _db, null);

    }

    public void stop(BundleContext bundleContext) throws Exception {
        //notify the OSGi framework this service is not longer available
        _registration.unregister();
        _db.shutdown();
        _db = null;
        
    }
}
