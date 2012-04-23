package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.statusservice.generated.ObjectFactory;
import edu.gemini.aspen.giapi.statusservice.generated.StatusType;
import edu.gemini.aspen.giapi.statusservice.generated.TranslateStatus;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
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
    public StatusItemTranslatorConfiguration(InputStream resourceAsStream) throws JAXBException {
        ClassLoader cl = ObjectFactory.class.getClassLoader();
        JAXBContext jaxbContext = JAXBContext.
                newInstance(ObjectFactory.class.getPackage().getName(), cl);
        Unmarshaller u = jaxbContext.createUnmarshaller();
        translatedStatuses = u.unmarshal(new StreamSource(resourceAsStream), TranslateStatus.class).getValue();
    }

    public List<StatusType> getStatuses() {
        return translatedStatuses.getStatus();
    }
}
