package edu.gemini.aspen.gmp.epics.simulator.osgi;

import edu.gemini.aspen.gmp.epics.simulator.SimulatedEpicsChannel;
import edu.gemini.aspen.gmp.epics.simulator.DataType;
import edu.gemini.aspen.gmp.epics.simulator.SimulatedEpicsConfiguration;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;

/**
 * An Epics Configuration built on top of the OSGI properties infraestructure
 */
public class OsgiSimulatedEpicsConfiguration implements SimulatedEpicsConfiguration {

    private static final Logger LOG = Logger.getLogger(OsgiSimulatedEpicsConfiguration.class.getName());
    private static final int MSEC_PER_SEC = 1000;
    private static final String CONF_FILE = "gmp.epics.simulation.conf";

    private Set<SimulatedEpicsChannel> _simChannels;


    public OsgiSimulatedEpicsConfiguration(BundleContext context) {
        Document doc = getPropertiesDocument(context);
        _simChannels = parseChannels(doc);

    }

    public Set<SimulatedEpicsChannel> getSimulataedChannels() {
        return Collections.unmodifiableSet(_simChannels);
    }


    private Document getPropertiesDocument(BundleContext ctx) {
        String configFileStr = getProperty(ctx, CONF_FILE);
        File confFile = new File(configFileStr);
        if (!confFile.exists()) {
            throw new RuntimeException("Missing properties config file: " + configFileStr);
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
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

    private Set<SimulatedEpicsChannel> parseChannels(Document doc) {

        Set<SimulatedEpicsChannel> channels = new HashSet<SimulatedEpicsChannel>();

        NodeList nodeList = doc.getElementsByTagName(CHANNEL_TAG);

        for (int i = 0; i < nodeList.getLength(); i++) {

            String name;
            int size;
            DataType type;
            long updateRate;

            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element firstElement = (Element) node;

                name = getNodeData(firstElement, NAME_TAG);
                if (name == null) {
                    LOG.warning("A simulated EPICS channel must have a name");
                    continue;
                }

                String typeString = getNodeData(firstElement, TYPE_TAG);
                if (typeString != null) {
                    try {
                        type = DataType.valueOf(typeString.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        LOG.warning("No valid data type for simulated EPICS channel: " + name);
                        continue;
                    }
                } else {
                    LOG.warning("No data type specified for simulated EPICS channel " + name);
                    continue;
                }

                String sizeString = getNodeData(firstElement, SIZE_TAG);
                if (sizeString != null) {
                    try {
                        size = Integer.parseInt(sizeString);
                    } catch (NumberFormatException e) {
                        LOG.warning("No valid size for simmulated EPICS channel " + name);
                        continue;
                    }
                } else {
                    LOG.warning("No size specified for simulated EPICS channel " + name);
                    continue;
                }

                String updateRateString = getNodeData(firstElement, UPDATE_RATE_TAG);

                if (updateRateString != null) {
                    try {
                        updateRate = Long.parseLong(updateRateString) * MSEC_PER_SEC;
                    } catch (NumberFormatException e) {
                        LOG.warning("No update rate specified for simulated EPICS channel " + name);
                        continue;
                    }
                } else {
                    LOG.warning("No update rate specified for simulated EPICS channel " + name);
                    continue;
                }

                if (name != null && type != null) {
                    channels.add(new SimulatedEpicsChannel(name, size, type, updateRate));
                }

            }

        }
        return channels;
    }


    /**
     * Just a simple auxiliary method to get the content of
     * an XML tag
     * @param element XML node to parse
     * @param tag Tag to parse
     * @return the string in the node. 
     */

    private String getNodeData(Element element, String tag) {

        if (element == null) return null;

        NodeList list = element.getElementsByTagName(tag);
        if (list == null) return null;

        Element nameElement = (Element) list.item(0);
        if (nameElement == null) return null;

        NodeList nameList = nameElement.getChildNodes();
        if (nameList == null) return null;

        Node nameNode = nameList.item(0);
        if (nameNode == null) return null;

        return nameNode.getNodeValue();
    }


    private String getProperty(BundleContext ctx, String key) {
        String res = ctx.getProperty(key);
        if (res == null) {
            throw new RuntimeException("Missing configuration: " + key);
        }

        return res;
    }

}