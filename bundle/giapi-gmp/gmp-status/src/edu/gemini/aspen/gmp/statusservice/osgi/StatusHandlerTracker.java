package edu.gemini.aspen.gmp.statusservice.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.logging.Logger;

import edu.gemini.aspen.gmp.statusservice.core.StatusHandlerRegister;
import edu.gemini.aspen.gmp.status.api.StatusHandler;

/**
 * This class tracks for the presence of a Status Handler services in the
 * OSGi framework. Once it founds the provider, register them with a
 * <code>StatusHandlerRegister</code>
 */
public class StatusHandlerTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(StatusHandlerTracker.class.getName());

    private StatusHandlerRegister _statusHandlerRegister;

    public StatusHandlerTracker(BundleContext ctx, StatusHandlerRegister shr) {
        super(ctx, StatusHandler.class.getName(), null);
        _statusHandlerRegister = shr;
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        StatusHandler handler = (StatusHandler) context.getService(serviceReference);

        LOG.info("Status handler added: " + handler);

        _statusHandlerRegister.addStatusHandler(handler);

        return handler;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {

        StatusHandler handler = (StatusHandler)context.getService(serviceReference);

        LOG.info("Status handler removed: " + handler);
        _statusHandlerRegister.removeStatusHandler(handler);

        context.ungetService(serviceReference);
    }
}
