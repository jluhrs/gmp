package edu.gemini.aspen.gmp.services.osgi;

import edu.gemini.aspen.gmp.services.properties.PropertyHolder;

import java.util.*;
import java.io.File;

import org.osgi.framework.BundleContext;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.io.SAXReader;

/**
 * A property holder that will get the properties 
 * from an XML configuration stored in the OSGi
 * bundle configuration.
 */
public class OsgiPropertyHolder implements PropertyHolder {

    private static final String CONF_FILE = "gmp.properties.conf";
    private static final String PROPERTY_TAG = "property";
    private static final String KEY_TAG = "key";

    private Map<String, String> _properties;

    public OsgiPropertyHolder(BundleContext ctx) {
        Document doc = getPropertiesDocument(ctx);
        _properties = parseProperties(doc);
    }


    public String getProperty(String key) {
        return _properties.get(key);
    }

    private Map<String, String> parseProperties(Document doc) {

        Element root = doc.getRootElement();
        Map<String, String> prop = new HashMap<String, String>();
        
        for (Iterator it = root.elementIterator(PROPERTY_TAG); it.hasNext(); ) {
            Element element = (Element)it.next();
            Attribute at = element.attribute(KEY_TAG);
            if (at == null) {
                throw new RuntimeException("Invalid Property. No Key found");
            }
            String key = at.getValue();
            String value = element.getTextTrim();
            prop.put(key, value);
        }

        return Collections.unmodifiableMap(prop);

    }

     private Document getPropertiesDocument(BundleContext ctx) {
        String configFileStr = getProperty(ctx, CONF_FILE);
        File confFile = new File(configFileStr);
        if (!confFile.exists()) {
            throw new RuntimeException("Missing properties config file: " + configFileStr);
        }
        SAXReader reader = new SAXReader();
        Document doc;
        try {
            doc = reader.read(confFile);
        } catch (DocumentException ex) {
            throw new RuntimeException("Could not load properties config file", ex);
        }

        return doc;
    }

    private String getProperty(BundleContext ctx, String key) {
        String res = ctx.getProperty(key);
        if (res == null) {
            throw new RuntimeException("Missing configuration: " + key);
        }

        return res;
    }


}
