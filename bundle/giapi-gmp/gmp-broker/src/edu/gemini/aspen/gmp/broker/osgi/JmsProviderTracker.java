package edu.gemini.aspen.gmp.broker.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.broker.api.GMPService;
import edu.gemini.aspen.gmp.broker.jms.JMSCompletionInfoConsumer;
import edu.gemini.aspen.gmp.broker.jms.JMSActionMessageProducer;
import edu.gemini.aspen.gmp.broker.jms.ActionSenderStrategy;
import edu.gemini.aspen.gmp.broker.impl.GMPServiceImpl;

import java.util.logging.Logger;

/**
 * This class tracks for the presence of a JMS Provider service in the
 * OSGi framework. Once it founds the provider, initializes the
 * GMP Service.
 */
public class JmsProviderTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(JmsProviderTracker.class.getName());


    private JMSCompletionInfoConsumer _completionConsumer;
    private JMSActionMessageProducer _actionMessageProducer;

    private GMPService _service = null;

    ServiceRegistration _registration;

    public JmsProviderTracker(BundleContext ctx) {
        super(ctx, JmsProvider.class.getName(), null);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        LOG.info("Adding JMS Service provider");
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);
        //start the action Message producer
        _actionMessageProducer = new JMSActionMessageProducer(provider);

        LOG.info("Starting GMP service bundle");
        _service = new GMPServiceImpl(new ActionSenderStrategy(_actionMessageProducer));
        _service.start();

        //start the Completion Info Consumer
        _completionConsumer = new JMSCompletionInfoConsumer(_service, provider);

        //advertise the GMP service in the OSGi framework
        _registration = context.registerService(
                GMPService.class.getName(),
                _service, null);

        return provider;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {

        LOG.info("Stopping GMP service bundle");

        _actionMessageProducer.close();
        _completionConsumer.close();

        _service.shutdown();

        //notify the OSGi framework this service is not longer available
        _registration.unregister();

        LOG.info("Removing JMS Service provider");
        context.ungetService(serviceReference);

        _actionMessageProducer = null;
        _completionConsumer = null;
    }



}
