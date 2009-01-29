package edu.gemini.aspen.gmp.statusgw.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleContext;
import edu.gemini.aspen.gmp.status.api.StatusDatabaseService;

import java.util.logging.Logger;

/**
 * Status database service tracker
 */
public class StatusDatabaseTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(StatusDatabaseTracker.class.getName());

    private Supervisor _supervisor;

    public StatusDatabaseTracker(BundleContext bundleContext, Supervisor supervisor) {
        super(bundleContext, StatusDatabaseService.class.getName(), null);
        _supervisor = supervisor;
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {

        LOG.info("Status Gateway has found a Status Database Service");
        StatusDatabaseService databaseService = (StatusDatabaseService) context.getService(serviceReference);
        _supervisor.registerDatabase(databaseService);
        _supervisor.start();
        return databaseService;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {
        LOG.info("Status Gateway has lost a Status Database Service");
        _supervisor.stop();
        _supervisor.unregisterDatabase();
        context.ungetService(serviceReference);
    }


}
