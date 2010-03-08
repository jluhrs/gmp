package edu.gemini.aspen.gmp.statusproc.jms.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.util.logging.Logger;

import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.statusproc.jms.JmsStatusProcessor;
import edu.gemini.aspen.gmp.status.api.StatusProcessor;

/**
 */
public class JmsProviderTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(JmsProviderTracker.class.getName());

    private ServiceRegistration _registration;

    private JmsStatusProcessor _jmsProcessor;

    public JmsProviderTracker(BundleContext ctx) {
        super(ctx, JmsProvider.class.getName(), null);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);
        LOG.info("Starting JMS Status Processor");

        _jmsProcessor = new JmsStatusProcessor(provider);

        //register this processor as a Status Processor in the OSGi
        _registration = context.registerService(
                StatusProcessor.class.getName(),
                _jmsProcessor, null);

        return provider;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {

        context.ungetService(serviceReference);

        //remove the service from the OSGi framework
        _registration.unregister();
        //stop the jms processor
        _jmsProcessor.stop();

        _jmsProcessor = null;


    }
}