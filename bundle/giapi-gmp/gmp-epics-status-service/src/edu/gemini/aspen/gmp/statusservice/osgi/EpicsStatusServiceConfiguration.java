package edu.gemini.aspen.gmp.statusservice.osgi;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An Epics Configuration built on top of the OSGI properties infraestructure
 */
public class EpicsStatusServiceConfiguration {

    private static final Logger LOG = Logger.getLogger(EpicsStatusServiceConfiguration.class.getName());

    private Channels _simChannels=null;


    public EpicsStatusServiceConfiguration(String confFileName) {
        if (validate(confFileName, confFileName.substring(0, confFileName.length() - 3).concat("xsd"))) {
            try {
                JAXBContext jc = JAXBContext.newInstance(Channels.class);
                _simChannels = (Channels) jc.createUnmarshaller().unmarshal(new File(confFileName));
            } catch (JAXBException ex) {
                LOG.log(Level.SEVERE, "Error parsing xml file " + confFileName, ex);
            }
        }
    }

    public List<Channels.ChannelConfig> getSimulatedChannels() {
        if(_simChannels==null){
            return new ArrayList<Channels.ChannelConfig>();
        }else{
            return Collections.unmodifiableList(_simChannels.getChannelConfig());
        }
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
            return false;
        }
        return true;
    }

    public static void main(String[] args){
        EpicsStatusServiceConfiguration ep=new EpicsStatusServiceConfiguration("/Users/nbarriga/Development/giapi-osgi/app/gmp-server/giapi-epics-status-mapping.xml");
        for(Channels.ChannelConfig it:ep.getSimulatedChannels()){
            System.out.println(it.getGiapiname() +" "+ it.getEpicsname() +" "+ it.getType() +" "+ it.getInitial());
        }
    }
}