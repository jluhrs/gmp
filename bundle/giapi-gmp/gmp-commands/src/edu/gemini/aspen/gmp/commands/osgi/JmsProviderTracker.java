package edu.gemini.aspen.gmp.commands.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import edu.gemini.jms.api.*;

import javax.jms.JMSException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class tracks for the presence of a JMS Provider service in the
 * OSGi framework. Once it founds the provider, initializes the
 * Sequence Command Service.
 */
public class JmsProviderTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(JmsProviderTracker.class.getName());


    private BaseMessageConsumer _messageConsumer;
    private BaseMessageProducer _messageProducer;

    public JmsProviderTracker(BundleContext ctx,
                              BaseMessageProducer producer,
                              BaseMessageConsumer consumer) {
        super(ctx, JmsProvider.class.getName(), null);
        _messageProducer = producer;
        _messageConsumer = consumer;

    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);

        try {
            _messageConsumer.startJms(provider);
            _messageProducer.startJms(provider);
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem starting message consumer", e);
        }

        LOG.info("Sequence Command JMS Producer and Consumer started.");

        return provider;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {

        _messageProducer.stopJms();
        _messageConsumer.stopJms();

        context.ungetService(serviceReference);

        LOG.info("Sequence Command JMS Producer and Consumer stopped.");

    }



}
