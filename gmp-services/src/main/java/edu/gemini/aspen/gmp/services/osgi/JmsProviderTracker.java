package edu.gemini.aspen.gmp.services.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.services.jms.RequestConsumer;
import edu.gemini.aspen.gmp.services.core.Service;
import edu.gemini.aspen.gmp.services.properties.PropertyService;

import java.util.logging.Logger;

/**
 * Tracks for a JMS provider and instantiate the request consumer
 * when a JMS provider is found
 */
public class JmsProviderTracker extends ServiceTracker {

    private final static Logger LOG = Logger.getLogger(JmsProviderTracker.class.getName());

    private static final String CONF_FILE = "gmp.properties.conf";

    private RequestConsumer _requestConsumer = null;
    private final String configurationFile;


    public JmsProviderTracker(BundleContext ctx) {
        super(ctx, JmsProvider.class.getName(), null);
        this.configurationFile = ctx.getProperty(CONF_FILE);
        if (configurationFile == null) {
            throw new RuntimeException("Missing configuration: " + CONF_FILE);
        }
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {

        LOG.info("JMS Provider found. Starting Services bundle");
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);

        _requestConsumer = new RequestConsumer(provider);

        //property service
        Service propertyService = new PropertyService(new XMLFileBasedPropertyHolder(configurationFile));
        _requestConsumer.registerService(propertyService);


        return provider;

    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {
        LOG.info("Stopping Services bundle");
        _requestConsumer.close();
        _requestConsumer = null;
    }
}
