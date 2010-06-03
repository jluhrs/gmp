package edu.gemini.aspen.gmp.statusservice.osgi;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.gmp.statusservice.StatusHandlerRegister;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * This class tracks for the presence of a Status Handler services in the
 * OSGi framework. Once it founds the provider, register them with a
 * <code>StatusHandlerRegister</code>
 */
public class StatusHandlerTracker extends ServiceTracker {

    private final StatusHandlerRegister _statusHandlerRegister;

    public StatusHandlerTracker(BundleContext ctx, StatusHandlerRegister shr) {
        super(ctx, StatusHandler.class.getName(), null);
        _statusHandlerRegister = shr;
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        StatusHandler handler = (StatusHandler) context.getService(serviceReference);

        _statusHandlerRegister.addStatusHandler(handler);

        return handler;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {

        StatusHandler handler = (StatusHandler)context.getService(serviceReference);

        _statusHandlerRegister.removeStatusHandler(handler);

        context.ungetService(serviceReference);
    }
}
