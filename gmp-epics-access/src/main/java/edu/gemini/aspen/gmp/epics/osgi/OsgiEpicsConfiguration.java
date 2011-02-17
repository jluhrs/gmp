package edu.gemini.aspen.gmp.epics.osgi;

import edu.gemini.aspen.gmp.epics.EpicsConfiguration;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;
import java.io.File;
import java.io.IOException;

/**
 * An Epics Configuration built on top of the OSGI properties infraestructure
 */
@Component(managedservice = "edu.gemini.aspen.gmp.epics.EpicsAccess")
@Instantiate
@Provides
public class OsgiEpicsConfiguration implements EpicsConfiguration {
    private static final String CONF_FILE = "gmp.epics.conf";
    private static final String CHANNEL_TAG = "channel";

    private Set<String> _validChannels;
    
    @Property(mandatory = true, value = CONF_FILE, name = "configurationFile")
    private String configFileStr;

    private OsgiEpicsConfiguration() {

    }

    public OsgiEpicsConfiguration(String configFileStr) {
        this.configFileStr = configFileStr;
        parseConfiguration();
    }

    @Validate
    public void parseConfiguration() {
        Document doc = getPropertiesDocument(configFileStr);
        _validChannels = parseChannels(doc);
    }

    public Set<String> getValidChannelsNames() {
        return Collections.unmodifiableSet(_validChannels);
    }


    private Document getPropertiesDocument(String configFileStr) {
        File confFile = new File(configFileStr);
        if (!confFile.exists()) {
            throw new RuntimeException("Missing properties config file: " + configFileStr);
        }

        DocumentBuilderFactory dbf  = DocumentBuilderFactory.newInstance();
        Document doc;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(confFile);
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException("Problem parsing epics configuration file", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Problem accessing epics configuration file", ex);
        } catch (SAXException ex) {
            throw new RuntimeException("Problem parsing epics configuration file", ex);
        }

        return doc;
    }

    private Set<String> parseChannels(Document doc) {

        Set<String> channels = new HashSet<String>();

        NodeList nodeList = doc.getElementsByTagName(CHANNEL_TAG);

        for (int i = 0; i < nodeList.getLength(); i++) {

            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NodeList list = node.getChildNodes();
                Node channelNode = list.item(0);
                if (channelNode != null) {
                    channels.add(channelNode.getNodeValue());
                }
            }

        }
        return channels;
    }

    private String getProperty(BundleContext ctx, String key) {
        String res = ctx.getProperty(key);
        if (res == null) {
            throw new RuntimeException("Missing configuration: " + key);
        }

        return res;
    }

}
