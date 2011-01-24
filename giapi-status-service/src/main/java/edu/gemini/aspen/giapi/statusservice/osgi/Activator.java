package edu.gemini.aspen.giapi.statusservice.osgi;

import edu.gemini.aspen.giapi.statusservice.StatusService;
import edu.gemini.jms.api.osgi.JmsProviderTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.logging.Logger;

/**
 * The OSGi Activator class for the Status Service
 */
public class Activator implements BundleActivator {

    public static final Logger LOG = Logger.getLogger(Activator.class.getName());

    private JmsProviderTracker _jmsTracker;

    private static final String STATUS_PROP = "edu.gemini.aspen.giapi.statusservice.jms.status";
    private static final String NAME_PROP =  "edu.gemini.aspen.giapi.statusservice.jms.name";

    private StatusHandlerTracker _statusHandlerTracker;

    public void start(BundleContext bundleContext) throws Exception {

        String statusName = bundleContext.getProperty(STATUS_PROP);

        String name = bundleContext.getProperty(NAME_PROP);

        StatusService statusService = new StatusService(name, statusName);

        //start tracking for a JMS provider to activate the status service
        _jmsTracker = new JmsProviderTracker(bundleContext, statusService);
        _jmsTracker.open();

        //start tracking status handlers
        _statusHandlerTracker = new StatusHandlerTracker(bundleContext, statusService);
        _statusHandlerTracker.open();


    }

    public void stop(BundleContext bundleContext) throws Exception {

        LOG.info("Stopping Status Consumer Service");
        _jmsTracker.close();
        _jmsTracker = null;

        _statusHandlerTracker.close();
        _statusHandlerTracker = null;
    }
}
