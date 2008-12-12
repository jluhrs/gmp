package edu.gemini.aspen.gmp.services.properties;

import java.util.Map;

/**
 *
 */
public class PropertyService {


    private final Map<String, String> _properties;
    
    public PropertyService(PropertyConfig config) {
        _properties = config.getProperties();
    }

    public String getProperty(String key) {
        return _properties.get(key);
    }


}
