package edu.gemini.aspen.gmp.statusdb.osgi;

import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import edu.gemini.aspen.gmp.statusdb.StatusDatabase;
import edu.gemini.aspen.giapi.status.StatusHandler;

/**
 * Activator class for the Status Database bundle
 */
public class Activator implements BundleActivator {

    private StatusDatabase _database;

    private ServiceRegistration _shRegistration;
    private ServiceRegistration _dbRegistration;

    public void start(BundleContext bundleContext) throws Exception {

        _database = new StatusDatabase();

        //advertise the Status Database into OSGi
        _shRegistration = bundleContext.registerService(
                StatusHandler.class.getName(),
                _database, null);

        //and advertise it as a Database Service as well.
        _dbRegistration = bundleContext.registerService(
                StatusDatabaseService.class.getName(),
                _database, null);

    }

    public void stop(BundleContext bundleContext) throws Exception {

        _database = null;

        _shRegistration.unregister();
        _dbRegistration.unregister();
    }
}
