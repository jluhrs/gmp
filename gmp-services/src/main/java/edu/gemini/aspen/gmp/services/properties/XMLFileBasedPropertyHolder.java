package edu.gemini.aspen.gmp.services.properties;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A property holder that will get the properties 
 * from an XML configuration stored in the OSGi
 * bundle configuration.
 */
public class XMLFileBasedPropertyHolder implements PropertyHolder {
    private static final String PROPERTY_TAG = "property";
    private static final String KEY_TAG = "key";

    private Map<String, String> _properties;

    public XMLFileBasedPropertyHolder(String configFileLocation) {
        Document doc = getPropertiesDocument(configFileLocation);
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

    private Document getPropertiesDocument(String configFileLocation) {
        File confFile = new File(configFileLocation);
        if (!confFile.exists()) {
            throw new RuntimeException("Missing properties config file: " + configFileLocation);
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

    @Override
    public String toString() {
        return "XMLFileBasedPropertyHolder{" +
                "_properties=" + _properties +
                '}';
    }
}
