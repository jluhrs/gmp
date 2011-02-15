package edu.gemini.aspen.gmp.epics.osgi;

import edu.gemini.aspen.gmp.epics.EpicsConfiguration;
import edu.gemini.aspen.gmp.epics.jms.EpicsConfigRequestConsumer;
import edu.gemini.jms.api.JmsProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import java.util.logging.Logger;

/**
 * Tracks for the JMS provider and instantiate the epics request consumer once
 * the provider is found.
 */
public class JmsProviderTracker extends ServiceTracker {

    private final static Logger LOG = Logger.getLogger(JmsProviderTracker.class.getName());


    private EpicsConfigRequestConsumer _epicsRequestConsumer;

    private EpicsConfiguration _epicsConfig;
    public JmsProviderTracker(BundleContext ctx, EpicsConfiguration config) {
        super(ctx, JmsProvider.class.getName(), null);
        _epicsConfig = config;
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {

        LOG.info("JMS Provider found. Starting Epics Access bundle");
        JmsProvider provider = (JmsProvider)context.getService(serviceReference);
        _epicsRequestConsumer =
                new EpicsConfigRequestConsumer(provider, _epicsConfig);
        return provider;

    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {
        LOG.info("Stopping Epics Access bundle");
        _epicsRequestConsumer.close();
        _epicsRequestConsumer = null;
    }

}
