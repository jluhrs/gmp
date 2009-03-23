package edu.gemini.aspen.gmp.epics.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.logging.Logger;

import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.epics.jms.EpicsConfigRequestConsumer;

/**
 * Tracks for the JMS provider and instantiate the epics request consumer once
 * the provider is found.
 */
public class JmsProviderTracker extends ServiceTracker {

    private final static Logger LOG = Logger.getLogger(JmsProviderTracker.class.getName());


    private EpicsConfigRequestConsumer _epicsRequestConsumer;

    public JmsProviderTracker(BundleContext ctx) {
        super(ctx, JmsProvider.class.getName(), null);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {

        LOG.info("JMS Provider found. Starting Epics Access bundle");
        JmsProvider provider = (JmsProvider)context.getService(serviceReference);
        _epicsRequestConsumer =
                new EpicsConfigRequestConsumer(provider,
                        new OsgiEpicsConfiguration(context));

        return provider;

    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {
        LOG.info("Stopping Epics Access bundle");
        _epicsRequestConsumer.close();
        _epicsRequestConsumer = null;
    }

}
