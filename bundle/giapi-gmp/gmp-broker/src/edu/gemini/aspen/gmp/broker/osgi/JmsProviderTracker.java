package edu.gemini.aspen.gmp.broker.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import edu.gemini.jms.api.Broker;
import edu.gemini.aspen.gmp.broker.api.GMPService;
import edu.gemini.aspen.gmp.broker.jms.JMSCompletionInfoConsumer;
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

    private GMPService _service = null;

    ServiceRegistration _registration;

    public JmsProviderTracker(BundleContext ctx) {
        super(ctx, Broker.class.getName(), null);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        LOG.info("Adding JMS Service provider");
        Broker service = (Broker)context.getService(serviceReference);

        LOG.info("Starting GMP service bundle");
        _service =  new GMPServiceImpl();
        _service.start();
        //advertise the GMP service in the OSGi framework
        _registration = context.registerService(
                GMPService.class.getName(),
                _service, null);

        //start the Completion Info Consumer
        _completionConsumer = new JMSCompletionInfoConsumer(_service);

        return service;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {

        LOG.info("Stopping GMP service bundle");

        _completionConsumer.close();

        _service.shutdown();

        //notify the OSGi framework this service is not longer available
        _registration.unregister();

        LOG.info("Removing JMS Service provider");
        context.ungetService(serviceReference);
    }



}
