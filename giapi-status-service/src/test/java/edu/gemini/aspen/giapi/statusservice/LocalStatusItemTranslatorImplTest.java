package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.aspen.gmp.top.TopImpl;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class LocalStatusItemTranslatorImplTest
 */
public class LocalStatusItemTranslatorImplTest {
    @Test
    public void testSimpleConfiguration() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        LocalStatusItemTranslatorImpl translator = new LocalStatusItemTranslatorImpl(top, null, file);
        translator.start();

        /*JmsProvider provider = mock(JmsProvider.class);
        translator.startJms(provider);*/
    }

    @Test
    public void testConfigurationWithSubstitution() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        System.setProperty("conf.file", "status-translator");
        String file = getClass().getResource("status-translator.xml").getFile();
        file = file.replace("status-translator", "${conf.file}");
        LocalStatusItemTranslatorImpl translator = new LocalStatusItemTranslatorImpl(top, null, file);
        translator.start();
    }

    @Test
    public void testTranslations() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        LocalStatusItemTranslatorImpl translator = new LocalStatusItemTranslatorImpl(top, null, file);
        translator.start();

        //map translation
        StatusItem good =new HealthStatus(top.buildStatusItemName("new"), Health.GOOD);
        List<StatusItem<?>> translated = translator.translate(new BasicStatus<Object>(top.buildStatusItemName("old"), 0));
        assertEquals(1,translated.size());
        assertEquals(good.getName(),translated.get(0).getName());
        assertEquals(good.getValue(),translated.get(0).getValue());
    }

    @Test
    public void testDefault() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        LocalStatusItemTranslatorImpl translator = new LocalStatusItemTranslatorImpl(top, null, file);
        translator.start();

        //default
        StatusItem bad =new HealthStatus(top.buildStatusItemName("new"), Health.BAD);
        List<StatusItem<?>> translated = translator.translate(new BasicStatus<Object>(top.buildStatusItemName("old"), 3));
        assertEquals(1, translated.size());
        assertEquals(bad.getName(), translated.get(0).getName());
        assertEquals(bad.getValue(),translated.get(0).getValue());

    }

    @Test
    public void testNonExistant() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        LocalStatusItemTranslatorImpl translator = new LocalStatusItemTranslatorImpl(top, null, file);
        translator.start();

        assertTrue(translator.translate(new BasicStatus<Object>(top.buildStatusItemName("inexistant translation"), 0)).isEmpty());

    }

    @Test
    public void testNoDefault() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        LocalStatusItemTranslatorImpl translator = new LocalStatusItemTranslatorImpl(top, null, file);
        translator.start();

        assertTrue(translator.translate(new BasicStatus<Object>(top.buildStatusItemName("oldnodefault"), 3)).isEmpty());

    }
}
