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
@Component(name="GMP Services", managedservice = "edu.gemini.aspen.gmp.services.GMPServices")
@Instantiate(name = "GMP Services")
public class GMPServices {
    private final static Logger LOG = Logger.getLogger(GMPServices.class.getName());
    private static final String CONF_FILE = "gmp.properties.conf";

    private RequestConsumer _requestConsumer = null;
    
    @Property(name = "propertiesFile", value = "NOVALID", mandatory = true)
    private String configurationFile;
    @Requires
    private JmsProvider provider;

    public GMPServices() {
    }

    @Validate
    public void startServices() {
        LOG.info("JMS Provider found. Starting Services bundle with configuration " + configurationFile);

        _requestConsumer = new RequestConsumer(provider);

        //property service
        Service propertyService = new PropertyService(new XMLFileBasedPropertyHolder(configurationFile));
        _requestConsumer.registerService(propertyService);
    }

    @Invalidate
    public void stopServices() {
        LOG.info("Stopping Services bundle");
        _requestConsumer.close();
        _requestConsumer = null;
    }
}
