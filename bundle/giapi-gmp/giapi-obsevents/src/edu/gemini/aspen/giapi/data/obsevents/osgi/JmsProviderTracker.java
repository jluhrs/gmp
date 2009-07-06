package edu.gemini.aspen.giapi.data.obsevents.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.logging.Logger;
import java.util.logging.Level;

import edu.gemini.jms.api.JmsProvider;
import edu.gemini.jms.api.BaseMessageConsumer;

import javax.jms.JMSException;

/**
 * 
 */
public class JmsProviderTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(JmsProviderTracker.class.getName());

    private BaseMessageConsumer _consumer;

    public JmsProviderTracker(BundleContext ctx, BaseMessageConsumer consumer) {
        super(ctx, JmsProvider.class.getName(), null);
        _consumer = consumer;
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        LOG.info("Adding JMS Service provider");
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);

        //start receiving observation event messages.
        try {
            _consumer.startJms(provider);
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem starting message consumer", e);
        }
        LOG.info("Starting to receive observation events");

        return provider;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {

        LOG.info("Stop receiving observation events");

        _consumer.stopJms();

        LOG.info("Removing JMS Service provider");
        context.ungetService(serviceReference);

    }
}

