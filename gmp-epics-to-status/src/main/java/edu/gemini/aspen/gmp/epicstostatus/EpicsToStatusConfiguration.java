package edu.gemini.aspen.gmp.epicstostatus;

import edu.gemini.aspen.gmp.epicstostatus.generated.Channels;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An Epics Configuration built on top of the OSGI properties infraestructure
 */
public class EpicsToStatusConfiguration {

    private static final Logger LOG = Logger.getLogger(EpicsToStatusConfiguration.class.getName());

    private final Channels _simChannels;

    public EpicsToStatusConfiguration(String confFileName, String schemaFileName) {
        Channels readChannels = new Channels();
        try {
            JAXBContext jc = JAXBContext.newInstance(Channels.class);
            Unmarshaller um = jc.createUnmarshaller();
            SchemaFactory factory =
                    SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = factory.newSchema(new File(schemaFileName));
            um.setSchema(schema); //to enable validation
            readChannels = (Channels) um.unmarshal(new File(confFileName));
        } catch (JAXBException ex) {
            LOG.log(Level.SEVERE, "Error parsing xml file " + confFileName, ex);
        } catch (SAXException ex) {
            LOG.log(Level.SEVERE, "Error parsing xml file " + confFileName, ex);
        }
        _simChannels = readChannels;
    }

    public Channels getSimulatedChannels() {
        return _simChannels;
    }

}