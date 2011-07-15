package edu.gemini.aspen.gmp.services;

import edu.gemini.aspen.gmp.services.core.Service;
import edu.gemini.aspen.gmp.services.jms.RequestConsumer;
import edu.gemini.aspen.gmp.services.properties.PropertyService;
import edu.gemini.aspen.gmp.services.properties.XMLFileBasedPropertyHolder;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.*;

import java.util.logging.Logger;

/**
 * Tracks for a JMS provider and instantiate the request consumer
 * when a JMS provider is found
 */
@Component
public class GMPServices {
    private final static Logger LOG = Logger.getLogger(GMPServices.class.getName());

    private final RequestConsumer _requestConsumer;

    private final String configurationFile;

    public GMPServices(@Requires JmsProvider provider,
        @Property(name = "propertiesFile", value = "NOVALID", mandatory = true) String configurationFile) {
        LOG.info("Starting Services bundle with configuration at " + configurationFile);

        _requestConsumer = new RequestConsumer(provider);
        this.configurationFile = configurationFile;
    }

    @Validate
    public void startServices() {
        LOG.info("Starting Services bundle");

        //property service
        Service propertyService = new PropertyService(new XMLFileBasedPropertyHolder(configurationFile));
        _requestConsumer.registerService(propertyService);
    }

    @Invalidate
    public void stopServices() {
        LOG.info("Stopping Services bundle");
        _requestConsumer.close();
    }
}
