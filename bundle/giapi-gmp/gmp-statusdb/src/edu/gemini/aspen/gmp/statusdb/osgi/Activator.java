package edu.gemini.aspen.gmp.statusdb.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import edu.gemini.aspen.gmp.statusdb.StatusDatabase;
import edu.gemini.aspen.gmp.status.api.StatusHandler;

/**
 * Activator class for the Status Database bundle
 */
public class Activator implements BundleActivator {

    private StatusProcessorTracker _tracker;
    private StatusDatabase _database;

    private ServiceRegistration _registration;

    public void start(BundleContext bundleContext) throws Exception {

        _database = new StatusDatabase();
        _database.start();

        //advertise the Status Database into OSGi
        _registration = bundleContext.registerService(
                StatusHandler.class.getName(),
                _database, null);

        //watch for status processors to be registered in this database
        _tracker = new StatusProcessorTracker(bundleContext, _database);
        _tracker.open();

    }

    public void stop(BundleContext bundleContext) throws Exception {

        _tracker.close();
        _tracker = null;

        _database.shutdown();

        _database = null;

        _registration.unregister();


    }
}
