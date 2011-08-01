package edu.gemini.aspen.gmp.services;

import edu.gemini.aspen.gmp.services.core.Service;
import edu.gemini.aspen.gmp.services.jms.RequestConsumer;
import edu.gemini.aspen.gmp.services.properties.PropertyHolder;
import edu.gemini.aspen.gmp.services.properties.PropertyService;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.logging.Logger;

/**
 * Tracks for a JMS provider and instantiate the request consumer
 * when a JMS provider is found
 */
@Component
public class GMPServices {
    private final static Logger LOG = Logger.getLogger(GMPServices.class.getName());

    private final RequestConsumer requestConsumer;
    private final PropertyHolder propertyHolder;

    public GMPServices(@Requires JmsProvider provider, @Requires PropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
        LOG.info("Starting Services bundle");

        requestConsumer = new RequestConsumer(provider);
    }

    @Validate
    public void startServices() {
        LOG.info("Starting Services bundle");

        //property service
        Service propertyService = new PropertyService(propertyHolder);
        requestConsumer.registerService(propertyService);
    }

    @Invalidate
    public void stopServices() {
        LOG.info("Stopping Services bundle");
        requestConsumer.close();
    }
}