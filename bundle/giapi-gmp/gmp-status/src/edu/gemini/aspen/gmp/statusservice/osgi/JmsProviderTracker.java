package edu.gemini.aspen.gmp.statusservice.osgi;


import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.statusservice.core.StatusUpdater;
import edu.gemini.aspen.gmp.statusservice.jms.JmsStatusConsumer;

/**
 * This class tracks for the presence of a JMS Provider service in the
 * OSGi framework. Once it founds the provider, initializes a
 * Status Consumer in the GMP.
 */
public class JmsProviderTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(JmsProviderTracker.class.getName());



    private JmsStatusConsumer _statusConsumer;
    private StatusUpdater _updater;


    public JmsProviderTracker(BundleContext ctx, StatusUpdater updater) {
        super(ctx, JmsProvider.class.getName(), null);
        _updater = updater;
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);
        LOG.info("Starting Status Consumer");
        _statusConsumer = new JmsStatusConsumer(provider, _updater);
        return provider;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {
        _statusConsumer.close();

        context.ungetService(serviceReference);
        LOG.info("Status service stopped");

    }
}
