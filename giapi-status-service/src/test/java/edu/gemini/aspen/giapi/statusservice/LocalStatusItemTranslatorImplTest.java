package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.aspen.gmp.top.TopImpl;
import org.junit.Test;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import java.io.IOException;

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
}
