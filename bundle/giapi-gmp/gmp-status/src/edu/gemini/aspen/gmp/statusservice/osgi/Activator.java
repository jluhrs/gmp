package edu.gemini.aspen.gmp.statusservice.osgi;

import edu.gemini.aspen.gmp.statusservice.jms.JmsStatusListener;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.osgi.JmsProviderTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.logging.Logger;

import edu.gemini.aspen.gmp.statusservice.StatusService;

/**
 * The OSGi Activator class for the Status Service
 */
public class Activator implements BundleActivator {

    public static final Logger LOG = Logger.getLogger(Activator.class.getName());

    private JmsProviderTracker _jmsTracker;

    private static final String TOPIC_PROP = "edu.gemini.aspen.gmp.statusservice.jms.destination";
    private static final String NAME_PROP =  "edu.gemini.aspen.gmp.statusservice.jms.name";

    private StatusHandlerTracker _statusHandlerTracker;

    private StatusService _statusService;

    public void start(BundleContext bundleContext) throws Exception {

        String destination = bundleContext.getProperty(TOPIC_PROP);

        if (destination == null) {
            destination = JmsStatusListener.TOPIC_NAME;
        }

        String name = bundleContext.getProperty(NAME_PROP);

        LOG.info("Starting Status Consumer Service");
        _statusService = new StatusService();

        BaseMessageConsumer consumer = new BaseMessageConsumer(
                name,
                new DestinationData(destination,
                        DestinationType.TOPIC),
                new JmsStatusListener(_statusService)
        );


        _jmsTracker = new JmsProviderTracker(bundleContext, name);
        _jmsTracker.registerJmsArtifact(consumer);
        _jmsTracker.open();

        _statusHandlerTracker = new StatusHandlerTracker(bundleContext, _statusService);
        _statusHandlerTracker.open();


    }

    public void stop(BundleContext bundleContext) throws Exception {

        LOG.info("Stopping Status Consumer Service");
        _jmsTracker.close();
        _jmsTracker = null;

        _statusHandlerTracker.close();
        _statusHandlerTracker = null;

        _statusService.shutdown();
        _statusService = null;
        
    }
}
