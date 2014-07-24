package edu.gemini.aspen.gmp.epics.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import edu.gemini.aspen.gmp.epics.EpicsConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * An Epics Configuration built on top of the OSGI properties infraestructure
 */
public class ChannelListConfiguration implements EpicsConfiguration {
    private static final String CHANNEL_TAG = "channel";

    private ImmutableSet<String> _validChannels;
    
    public ChannelListConfiguration(String configFileStr) {
        Document doc = getPropertiesDocument(configFileStr);
        _validChannels = parseChannels(doc);
    }

    public Set<String> getValidChannelsNames() {
        return _validChannels;
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

    private ImmutableSet<String> parseChannels(Document doc) {
        Set<String> channels = Sets.newHashSet();

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
        return ImmutableSet.copyOf(channels);
    }

}
