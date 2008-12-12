package edu.gemini.aspen.gmp.services.properties;

import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class PropertyService {



    private static final Logger LOG = Logger.getLogger(PropertyService.class.getName());

    private final Map<String, String> _properties;
    
    public PropertyService() {
        _properties = new HashMap<String, String>();
        //read the properties from a configuration file

    }

    public String getProperty(String key) {
        LOG.info("Requested Property :" + key);
        return "PROPERTY";
    }


}
