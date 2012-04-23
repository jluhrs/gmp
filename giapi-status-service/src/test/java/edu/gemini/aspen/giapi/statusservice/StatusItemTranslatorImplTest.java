package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.aspen.gmp.top.TopImpl;
import org.junit.Test;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

/**
 * Class StatusItemTranslatorImplTest
 */
public class StatusItemTranslatorImplTest {
    @Test
    public void testSimpleConfiguration() throws JAXBException, FileNotFoundException, JMSException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        StatusItemTranslatorImpl translator = new StatusItemTranslatorImpl(top, file);
        translator.start();

        /*JmsProvider provider = mock(JmsProvider.class);
        translator.startJms(provider);*/
    }

    @Test
    public void testConfigurationWithSubstitution() throws JAXBException, FileNotFoundException, JMSException {
        Top top = new TopImpl("gpi", "gpi");
        System.setProperty("conf.file", "status-translator");
        String file = getClass().getResource("status-translator.xml").getFile();
        file = file.replace("status-translator", "${conf.file}");
        StatusItemTranslatorImpl translator = new StatusItemTranslatorImpl(top, file);
        translator.start();
    }
}
