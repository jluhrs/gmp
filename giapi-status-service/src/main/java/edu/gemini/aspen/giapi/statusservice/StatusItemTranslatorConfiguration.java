package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.statusservice.generated.StatusType;
import edu.gemini.aspen.giapi.statusservice.generated.TranslateStatus;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.util.List;

/**
 * Class StatusItemTranslatorConfiguration
 *
 * @author Nicolas A. Barriga
 *         Date: 4/9/12
 */
public class StatusItemTranslatorConfiguration {
    private final TranslateStatus translatedStatuses;

    public StatusItemTranslatorConfiguration(InputStream resourceAsStream) throws JAXBException, SAXException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TranslateStatus.class);
        Unmarshaller u = jaxbContext.createUnmarshaller();
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        Schema schema = factory.newSchema(this.getClass().getResource("status-translator.xsd"));
        u.setSchema(schema); //to enable validation

        translatedStatuses = u.unmarshal(new StreamSource(resourceAsStream), TranslateStatus.class).getValue();
    }

    public List<StatusType> getStatuses() {
        return translatedStatuses.getStatus();
    }
}
