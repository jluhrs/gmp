package edu.gemini.aspen.giapi.status.dispatcher.osgi;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.dispatcher.FilteredStatusHandler;
import edu.gemini.aspen.giapi.status.dispatcher.StatusDispatcher;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private ServiceRegistration<StatusHandler> serviceRegistration = null;
    private ServiceTracker<FilteredStatusHandler, FilteredStatusHandler> serviceTracker = null;

    @Override
    public void start(BundleContext context) {
        StatusDispatcher statusDispatcher = new StatusDispatcher();
        serviceRegistration = context.registerService(StatusHandler.class, statusDispatcher, new Hashtable<String, String>());
        serviceTracker = new ServiceTracker<>(context, FilteredStatusHandler.class, new FSHTracker(context, statusDispatcher));
        serviceTracker.open(true);
    }

    @Override
    public void stop(BundleContext context) {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            serviceRegistration = null;
        }
        if (serviceTracker != null) {
            serviceTracker.close();
            serviceTracker = null;
        }
    }

    private static class FSHTracker implements ServiceTrackerCustomizer<FilteredStatusHandler, FilteredStatusHandler> {
        private final BundleContext context;
        private final StatusDispatcher statusDispatcher;

        public FSHTracker(BundleContext context, StatusDispatcher statusDispatcher) {
            this.context = context;
            this.statusDispatcher = statusDispatcher;
        }

        @Override
        public FilteredStatusHandler addingService(ServiceReference<FilteredStatusHandler> reference) {
            FilteredStatusHandler statusHandler = context.getService(reference);
            statusDispatcher.bindStatusHandler(statusHandler);
            return statusHandler;
        }

        @Override
        public void modifiedService(ServiceReference<FilteredStatusHandler> filteredStatusHandlerServiceReference, FilteredStatusHandler filteredStatusHandler) {

        }

        @Override
        public void removedService(ServiceReference<FilteredStatusHandler> reference, FilteredStatusHandler statusHandler) {
            statusDispatcher.unbindStatusHandler(statusHandler);
        }
    }
}
