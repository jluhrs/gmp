package edu.gemini.aspen.giapi.statusservice.osgi;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregate;
import edu.gemini.aspen.giapi.statusservice.StatusService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Hashtable;

public class Activator implements BundleActivator {
    private final StatusHandlerAggregate tracker = new StatusHandlerAggregate();
    private ServiceTracker<StatusHandler, StatusHandler> statusHandlerServiceTracker;
    private ServiceRegistration<ManagedServiceFactory> factoryService;
    private ServiceRegistration<StatusHandlerAggregate> aggregateService;

    @Override
    public void start(final BundleContext bundleContext) {
        statusHandlerServiceTracker = new ServiceTracker<>(bundleContext, StatusHandler.class, new ServiceTrackerCustomizer<StatusHandler, StatusHandler>() {
            @Override
            public StatusHandler addingService(ServiceReference<StatusHandler> reference) {
                StatusHandler statusHandler = bundleContext.getService(reference);
                tracker.bindStatusHandler(statusHandler);
                return statusHandler;
            }

            @Override
            public void modifiedService(ServiceReference<StatusHandler> statusHandlerServiceReference, StatusHandler statusHandler) {

            }

            @Override
            public void removedService(ServiceReference<StatusHandler> statusHandlerServiceReference, StatusHandler statusHandler) {
                tracker.unbindStatusHandler(statusHandler);
            }
        });
        statusHandlerServiceTracker.open(true);

        Hashtable<String, String> props = new Hashtable<>();
        props.put("service.pid", StatusService.class.getName());

        StatusServiceFactory serviceFactory = new StatusServiceFactory(tracker, bundleContext);
        factoryService = bundleContext.registerService(ManagedServiceFactory.class, serviceFactory, props);

        aggregateService = bundleContext.registerService(StatusHandlerAggregate.class, tracker, new Hashtable<String, String>());
    }

    @Override
    public void stop(BundleContext bundleContext) {
        tracker.cleanHandlers();
        statusHandlerServiceTracker.close();
        if (factoryService != null) {
            factoryService.unregister();
            factoryService = null;
        }
        if (aggregateService != null) {
            aggregateService.unregister();
            aggregateService = null;
        }
    }
}
