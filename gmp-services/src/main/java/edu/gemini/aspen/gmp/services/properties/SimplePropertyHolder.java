package edu.gemini.aspen.gmp.services.properties;

import edu.gemini.aspen.gmp.services.PropertyHolder;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * A property holder that will get the properties
 * directly from OSGi bundle configuration.
 */
@Component
@Provides
public class SimplePropertyHolder implements PropertyHolder, ManagedService {

    @Property(name = "GMP_HOST_NAME", value = "NO_VALID", mandatory = true)
    public void setGMP_HOST_NAME(String GMP_HOST_NAME) {
        _properties.put(GmpProperties.GMP_HOST_NAME.name(), GMP_HOST_NAME);
    }

    @Property(name = "DHS_ANCILLARY_DATA_PATH", value = "NO_VALID", mandatory = true)
    public void setDHS_ANCILLARY_DATA_PATH(String DHS_ANCILLARY_DATA_PATH) {
        _properties.put(GmpProperties.DHS_ANCILLARY_DATA_PATH.name(), DHS_ANCILLARY_DATA_PATH);
    }

    @Property(name = "DHS_SCIENCE_DATA_PATH", value = "NO_VALID", mandatory = true)
    public void setDHS_SCIENCE_DATA_PATH(String DHS_SCIENCE_DATA_PATH) {
        _properties.put(GmpProperties.DHS_SCIENCE_DATA_PATH.name(), DHS_SCIENCE_DATA_PATH);
    }

    @Property(name = "DHS_INTERMEDIATE_DATA_PATH", value = "NO_VALID", mandatory = true)
    public void setDHS_INTERMEDIATE_DATA_PATH(String DHS_INTERMEDIATE_DATA_PATH) {
        _properties.put(GmpProperties.DHS_INTERMEDIATE_DATA_PATH.name(), DHS_INTERMEDIATE_DATA_PATH);
    }

    @Property(name = "DEFAULT", value = "NO_VALID", mandatory = true)
    public void setDEFAULT(String DEFAULT) {
        _properties.put(GmpProperties.DEFAULT.name(), DEFAULT);
    }


    private Map<String, String> _properties = new HashMap<String, String>();


    @Validate
    public void validate() {

    }

    @Invalidate
    public void inValidate() {

    }

    @Override
    public void updated(Dictionary dictionary) throws ConfigurationException {
        Dictionary<String, String> dict = dictionary;
        Enumeration<String> elems = dict.keys();
        for (String property = elems.nextElement(); elems.hasMoreElements(); property = elems.nextElement()) {
            _properties.put(property, dict.get(property));
        }
    }

    @Override
    public String getProperty(String key) {
        return _properties.get(key);
    }
}
