package edu.gemini.aspen.gmp.services.properties;

import edu.gemini.aspen.gmp.services.PropertyHolder;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * A property holder that will get the properties
 * directly from OSGi bundle configuration.
 */
public class SimplePropertyHolder implements PropertyHolder {
    private Map<String, String> _properties = new HashMap<String, String>();

    public SimplePropertyHolder(Dictionary<String, ?> dictionary) {
        Dictionary<String, ?> dict = dictionary;
        Enumeration<String> elems = dict.keys();
        for (String property = elems.nextElement(); elems.hasMoreElements(); property = elems.nextElement()) {
            _properties.put(property, dict.get(property).toString());
        }
    }

    @Override
    public String getProperty(String key) {
        String storedVal = _properties.get(key);
        if (storedVal != null) {
            return storedVal;
        } else {
            try {
                return GmpProperties.valueOf(key).getDefault();
            } catch (IllegalArgumentException ex) {
                return "";
            }
        }
    }
}
