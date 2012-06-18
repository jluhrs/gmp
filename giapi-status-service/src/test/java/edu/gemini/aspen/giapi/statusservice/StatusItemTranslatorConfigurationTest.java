package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.statusservice.generated.DataType;
import edu.gemini.aspen.giapi.statusservice.generated.StatusType;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Class StatusItemTranslatorConfigurationTest
 *
 * @author Nicolas A. Barriga
 *         Date: 4/9/12
 */
public class StatusItemTranslatorConfigurationTest {
    @Test
    public void testSimpleConfiguration() throws JAXBException, SAXException {
        StatusItemTranslatorConfiguration configuration = new StatusItemTranslatorConfiguration(getClass().getResourceAsStream("status-translator.xml"));
        List<StatusType> statuses = configuration.getStatuses();
        assertEquals(2, statuses.size());
        assertEquals("old", statuses.get(0).getOriginalName());
        assertEquals("new", statuses.get(0).getTranslatedName());
        assertEquals(DataType.INT, statuses.get(0).getOriginalType());
        assertEquals(DataType.HEALTH, statuses.get(0).getTranslatedType());
        for (int i = 0; i < 2; i++) {
            assertEquals(Integer.toString(i), statuses.get(0).getMaps().getMap().get(i).getFrom());
            assertEquals(Health.values()[i], Health.valueOf(statuses.get(0).getMaps().getMap().get(i).getTo()));
        }
        assertEquals("BAD", statuses.get(0).getDefault());
        assertEquals(null, statuses.get(1).getDefault());
    }
}
