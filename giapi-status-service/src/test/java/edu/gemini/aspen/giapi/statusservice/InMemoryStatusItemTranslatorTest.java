package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.gmp.statusdb.StatusDatabase;
import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.aspen.gmp.top.TopImpl;
import edu.gemini.shared.util.immutable.None;
import edu.gemini.shared.util.immutable.Option;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Class InMemoryStatusItemTranslatorTest
 */
public class InMemoryStatusItemTranslatorTest {
    @Test
    public void testSimpleConfiguration() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        StatusDatabaseService db = new StatusDatabase();
        StatusHandlerAggregate aggregate = mock(StatusHandlerAggregate.class);
        InMemoryStatusItemTranslator translator = new InMemoryStatusItemTranslator(top, aggregate, db, file);
        translator.start();

        verifyZeroInteractions(aggregate);
    }

    @Test
    public void testTranslations() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        StatusDatabaseService db = new StatusDatabase();
        StatusHandlerAggregate aggregate = mock(StatusHandlerAggregate.class);
        InMemoryStatusItemTranslator  translator = new InMemoryStatusItemTranslator (top, aggregate, db, file);
        translator.start();

        //map translation
        StatusItem good =new HealthStatus(top.buildStatusItemName("new"), Health.GOOD);
        Option<StatusItem<?>> translated = translator.translate(new BasicStatus<Object>(top.buildStatusItemName("old"), 0));
        assertNotSame(None.instance(),translated);
        assertEquals(good.getName(),translated.getValue().getName());
        assertEquals(good.getValue(),translated.getValue().getValue());
    }

    @Test
    public void testTranslationUpdate() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        StatusDatabaseService db = new StatusDatabase();
        StatusHandlerAggregate aggregate = mock(StatusHandlerAggregate.class);
        InMemoryStatusItemTranslator  translator = new InMemoryStatusItemTranslator (top, aggregate, db, file);
        translator.start();

        //map translation
        StatusItem good =new HealthStatus(top.buildStatusItemName("new"), Health.GOOD);
        translator.update(new BasicStatus<Object>(top.buildStatusItemName("old"), 0));
        verify(aggregate).update(any(StatusItem.class));
    }

    @Test
    public void testTranslateOnStart() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        StatusDatabase db = new StatusDatabase();
        StatusHandlerAggregate aggregate = mock(StatusHandlerAggregate.class);

        StatusItem original = new BasicStatus<Object>(top.buildStatusItemName("old"), 3);
        // This is unknown
        StatusItem original2 = new BasicStatus<Object>(top.buildStatusItemName("oldone"), 3);
        //when(db.getAll()).thenReturn(ImmutableList.of(original));
        db.update(original);
        db.update(original2);

        InMemoryStatusItemTranslator  translator = new InMemoryStatusItemTranslator (top, aggregate, db, file);
        translator.start();

        // only called once
        verify(aggregate, times(1)).update(any(StatusItem.class));
    }
}
