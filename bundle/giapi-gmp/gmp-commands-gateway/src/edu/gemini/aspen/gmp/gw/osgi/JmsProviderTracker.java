package edu.gemini.aspen.gmp.gw.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleContext;
import edu.gemini.jms.api.JmsProvider;

import java.util.logging.Logger;

/**
 */
public class JmsProviderTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(JmsProviderTracker.class.getName());

    private Supervisor _supervisor;
    public JmsProviderTracker(BundleContext bundleContext, Supervisor supervisor) {
        super(bundleContext, JmsProvider.class.getName(), null);
        _supervisor = supervisor;
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {

        LOG.info("GMP Gateway has found JMS Provider");
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);
        _supervisor.registerProvider(provider);
        _supervisor.start();
        return provider;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {
        LOG.info("GMP Gateway has lost JMS Provider. Stopping");
        _supervisor.stop();
        _supervisor.unregisterProvider();
        LOG.info("Removing JMS Service provider");
        context.ungetService(serviceReference);
        
    }
}
