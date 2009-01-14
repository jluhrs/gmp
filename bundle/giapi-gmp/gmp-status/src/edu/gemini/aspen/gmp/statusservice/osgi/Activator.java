package edu.gemini.aspen.gmp.statusservice.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.logging.Logger;

import edu.gemini.aspen.gmp.statusservice.core.StatusService;

/**
 * The OSGi Activator class for the Status Service
 */
public class Activator implements BundleActivator {

    public static final Logger LOG = Logger.getLogger(Activator.class.getName());

    private JmsProviderTracker _jmsTracker;

    private StatusHandlerTracker _statusHandlerTracker;

    private StatusService _statusService;

    public void start(BundleContext bundleContext) throws Exception {

        _statusService = new StatusService();
        _statusService.start();

        _jmsTracker = new JmsProviderTracker(bundleContext, _statusService);
        _jmsTracker.open();

        _statusHandlerTracker = new StatusHandlerTracker(bundleContext, _statusService);
        _statusHandlerTracker.open();


    }

    public void stop(BundleContext bundleContext) throws Exception {

        _jmsTracker.close();
        _jmsTracker = null;

        _statusHandlerTracker.close();
        _statusHandlerTracker = null;

        _statusService.shutdown();
        _statusService = null;
        
    }
}
