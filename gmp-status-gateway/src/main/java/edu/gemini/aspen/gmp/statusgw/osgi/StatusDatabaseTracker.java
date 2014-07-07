package edu.gemini.aspen.gmp.statusgw.osgi;

import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import edu.gemini.aspen.gmp.statusgw.StatusDatabaseServiceDecorator;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleContext;

import java.util.logging.Logger;

/**
 * Status database service tracker
 */
public class StatusDatabaseTracker extends ServiceTracker<StatusDatabaseService, StatusDatabaseService> {

    private static final Logger LOG = Logger.getLogger(StatusDatabaseTracker.class.getName());

    private final StatusDatabaseServiceDecorator _serviceDecorator;

    public StatusDatabaseTracker(BundleContext bundleContext, StatusDatabaseServiceDecorator decorator) {
        super(bundleContext, StatusDatabaseService.class.getName(), null);
        _serviceDecorator = decorator;
    }

    @Override
    public StatusDatabaseService addingService(ServiceReference<StatusDatabaseService> serviceReference) {

        LOG.info("Status Gateway has found a Status Database Service");
        StatusDatabaseService databaseService = context.getService(serviceReference);
        _serviceDecorator.setDatabaseService(databaseService);
        return databaseService;
    }

    @Override
    public void removedService(ServiceReference<StatusDatabaseService> serviceReference, StatusDatabaseService o) {
        LOG.info("Status Gateway has lost a Status Database Service");
        _serviceDecorator.removeDatabaseService();
        context.ungetService(serviceReference);
    }


}
