package edu.gemini.aspen.gmp.statusservice.osgi;

import gov.aps.jca.dbr.DBRType;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An Epics Configuration built on top of the OSGI properties infraestructure
 */
public class EpicsStatusServiceConfiguration {

    private static final Logger LOG = Logger.getLogger(EpicsStatusServiceConfiguration.class.getName());
    static final String CHANNEL_TAG = "channel";
    static final String GIAPI_NAME_TAG = "giapiname";
    static final String EPICS_NAME_TAG = "epicsname";
    static final String TYPE_TAG = "type";
    static final String INITIAL_TAG = "initial";

    private Set<ChannelConfig> _simChannels;

    public class ChannelConfig {
        public String giapiName;
        public String epicsName;
        public DBRType type;
        public Object initialValue;
    }

    public EpicsStatusServiceConfiguration(String confFileName) {
        Document doc = getPropertiesDocument(confFileName);
        _simChannels = parseChannels(doc);

    }

    public Set<ChannelConfig> getSimulatedChannels() {
        return Collections.unmodifiableSet(_simChannels);
    }

    private boolean validate(String xml, String xsd) {
        // 1. Lookup a factory for the W3C XML Schema language
        SchemaFactory factory =
                SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        // 2. Compile the schema.
        // Here the schema is loaded from a java.io.File, but you could use
        // a java.net.URL or a javax.xml.transform.Source instead.
        File schemaLocation = new File(xsd);
        try {
            Schema schema = factory.newSchema(schemaLocation);

            // 3. Get a validator from the schema.
            Validator validator = schema.newValidator();

            // 4. Parse the document you want to check.
            Source source = new StreamSource(xml);

            // 5. Check the document

            validator.validate(source);
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE,"Validating XML file: '"+xml+"', using XSD: '"+xsd+"' failed.",ex);
        }
        return true;
    }
    
    private Document getPropertiesDocument(String filename) {

        //Just log a warning if it doesn't validate.
        //Eventually we will change this to take action.
        validate(filename,filename.substring(0,filename.length()-3).concat("xsd"));

        File confFile = new File(filename);
        if (!confFile.exists()) {
            throw new RuntimeException("Missing properties config file: " + filename);
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

    private Set<ChannelConfig> parseChannels(Document doc) {

        Set<ChannelConfig> channels = new HashSet<ChannelConfig>();

        NodeList nodeList = doc.getElementsByTagName(CHANNEL_TAG);

        for (int i = 0; i < nodeList.getLength(); i++) {

            String giapiName, epicsName;
            Object initial;
            DBRType type;

            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element firstElement = (Element) node;

                giapiName = getNodeData(firstElement, GIAPI_NAME_TAG);
                if (giapiName == null) {
                    LOG.warning("A simulated EPICS channel must have a name");
                    continue;
                }
                epicsName = getNodeData(firstElement, EPICS_NAME_TAG);
                if (epicsName == null) {
                    LOG.warning("A simulated EPICS channel must have a name");
                    continue;
                }
                String typeString = getNodeData(firstElement, TYPE_TAG);
                if (typeString != null) {
                    try {
                        if(typeString.equals("INT")){
                            type = DBRType.INT;
                        }else if(typeString.equals("FLOAT")){
                            type = DBRType.FLOAT;
                        }else if(typeString.equals("DOUBLE")){
                            type = DBRType.DOUBLE;
                        }else if(typeString.equals("STRING")){
                            type = DBRType.STRING;
                        }else{
                            throw new IllegalArgumentException("Unsupported data type "+typeString);
                        }
                    } catch (IllegalArgumentException e) {
                        LOG.warning("No valid data type for simulated EPICS channel: " + epicsName);
                        continue;
                    }
                } else {
                    LOG.warning("No data type specified for simulated EPICS channel " + epicsName);
                    continue;
                }

                String valueString = getNodeData(firstElement, INITIAL_TAG);
                if (valueString != null) {
                    try {
                        //TODO: support multiple values (array)
                        if(type.equals(DBRType.INT)){
                            initial = Integer.parseInt(valueString);
                        }else if(type.equals(DBRType.FLOAT)){
                            initial = Float.parseFloat(valueString);
                        }else if(type.equals(DBRType.DOUBLE)){
                            initial = Double.parseDouble(valueString);
                        }else if(type.equals(DBRType.STRING)){
                            initial = valueString;
                        }else{
                            throw new IllegalArgumentException("Unsupported data type "+type);
                        }
                    } catch (NumberFormatException e) {
                        LOG.warning("No valid size for simulated EPICS channel " + epicsName);
                        continue;
                    }
                } else {
                    LOG.warning("No size specified for simulated EPICS channel " + epicsName);
                    continue;
                }
                ChannelConfig item=    new ChannelConfig();
                item.giapiName=giapiName;
                item.epicsName=epicsName;
                item.type=type;
                item.initialValue=initial;
                channels.add(item);

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

    /**
     * Just a simple auxiliary method to get the content of
     * an XML tag
     * @param element XML node to parse
     * @param tag Tag to parse
     * @return the string in the node.
     */

    private List<String> getMultipleNodeData(Element element, String tag) {

        if (element == null) return null;

        NodeList list = element.getElementsByTagName(tag);
        if (list == null) return null;

        Element nameElement = (Element) list.item(0);
        if (nameElement == null) return null;

        NodeList nameList = nameElement.getChildNodes();
        if (nameList == null) return null;

        Node nameNode = nameList.item(0);
        if (nameNode == null) return null;

        List<String> values=new ArrayList<String>();
        for(int i=0;i<nameList.getLength();i++){
            values.add(nameList.item(i).getNodeValue());
        }

        return values;
    }


    private String getProperty(BundleContext ctx, String key) {
        String res = ctx.getProperty(key);
        if (res == null) {
            throw new RuntimeException("Missing configuration: " + key);
        }

        return res;
    }

    public static void main(String[] args){
        EpicsStatusServiceConfiguration ep=new EpicsStatusServiceConfiguration("/Users/nbarriga/Development/giapi-osgi/app/gmp-server/giapi-epics-status-mapping.xml");
        for(ChannelConfig it:ep.getSimulatedChannels()){
            System.out.println(it.giapiName);
        }
    }
}