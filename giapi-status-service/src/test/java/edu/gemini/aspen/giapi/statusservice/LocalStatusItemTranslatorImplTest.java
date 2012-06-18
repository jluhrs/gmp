package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.aspen.gmp.top.TopImpl;
import edu.gemini.shared.util.immutable.None;
import edu.gemini.shared.util.immutable.Option;
import org.junit.Test;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Class LocalStatusItemTranslatorImplTest
 */
public class LocalStatusItemTranslatorImplTest {
    @Test
    public void testSimpleConfiguration() throws JAXBException, IOException, JMSException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        LocalStatusItemTranslatorImpl translator = new LocalStatusItemTranslatorImpl(top, null, file);
        translator.start();

        /*JmsProvider provider = mock(JmsProvider.class);
        translator.startJms(provider);*/
    }

    @Test
    public void testConfigurationWithSubstitution() throws JAXBException, IOException, JMSException {
        Top top = new TopImpl("gpi", "gpi");
        System.setProperty("conf.file", "status-translator");
        String file = getClass().getResource("status-translator.xml").getFile();
        file = file.replace("status-translator", "${conf.file}");
        LocalStatusItemTranslatorImpl translator = new LocalStatusItemTranslatorImpl(top, null, file);
        translator.start();
    }

    @Test
    public void testTranslations() throws JAXBException, IOException, JMSException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        LocalStatusItemTranslatorImpl translator = new LocalStatusItemTranslatorImpl(top, null, file);
        translator.start();

        //map translation
        StatusItem good =new HealthStatus(top.buildStatusItemName("new"), Health.GOOD);
        Option<StatusItem<?>> translated = translator.translate(new BasicStatus<Object>(top.buildStatusItemName("old"), 0));
        assertNotSame(None.instance(),translated);
        assertEquals(good.getName(),translated.getValue().getName());
        assertEquals(good.getValue(),translated.getValue().getValue());
    }

    @Test
    public void testDefault() throws JAXBException, IOException, JMSException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        LocalStatusItemTranslatorImpl translator = new LocalStatusItemTranslatorImpl(top, null, file);
        translator.start();

        //default
        StatusItem bad =new HealthStatus(top.buildStatusItemName("new"), Health.BAD);
        Option<StatusItem<?>> translated = translator.translate(new BasicStatus<Object>(top.buildStatusItemName("old"), 3));
        assertNotSame(None.instance(),translated);
        assertEquals(bad.getName(), translated.getValue().getName());
        assertEquals(bad.getValue(),translated.getValue().getValue());

    }

    @Test
    public void testNonExistant() throws JAXBException, IOException, JMSException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        LocalStatusItemTranslatorImpl translator = new LocalStatusItemTranslatorImpl(top, null, file);
        translator.start();

        assertEquals(None.instance(),translator.translate(new BasicStatus<Object>(top.buildStatusItemName("inexistant translation"), 0)));

    }

    @Test
    public void testNoDefault() throws JAXBException, IOException, JMSException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        LocalStatusItemTranslatorImpl translator = new LocalStatusItemTranslatorImpl(top, null, file);
        translator.start();

        assertEquals(None.instance(),translator.translate(new BasicStatus<Object>(top.buildStatusItemName("oldnodefault"), 3)));

    }
}
