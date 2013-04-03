package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.gmp.statusdb.StatusDatabase;
import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.aspen.gmp.top.TopImpl;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        List<StatusItem<?>> translated = translator.translate(new BasicStatus<Object>(top.buildStatusItemName("old"), 0));
        assertFalse(translated.isEmpty());
        assertEquals(good.getName(),translated.get(0).getName());
        assertEquals(good.getValue(),translated.get(0).getValue());
    }

    @Test
    public void testMultipleTranslationsSameOrigin() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        StatusDatabaseService db = new StatusDatabase();
        StatusHandlerAggregate aggregate = mock(StatusHandlerAggregate.class);
        InMemoryStatusItemTranslator  translator = new InMemoryStatusItemTranslator (top, aggregate, db, file);
        translator.start();

        //map translation
        List<StatusItem<?>> translated = translator.translate(new BasicStatus<Object>(top.buildStatusItemName("twice"), 0));
        assertEquals(2, translated.size());

        StatusItem first =new HealthStatus(top.buildStatusItemName("first"), Health.GOOD);
        assertEquals(first.getName(),translated.get(1).getName());
        assertEquals(first.getValue(),translated.get(1).getValue());

        StatusItem second =new HealthStatus(top.buildStatusItemName("second"), Health.GOOD);
        assertEquals(second.getName(),translated.get(0).getName());
        assertEquals(second.getValue(),translated.get(0).getValue());
    }
    @Test
    public void testMultipleOriginsSameTranslation() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        StatusDatabaseService db = new StatusDatabase();
        StatusHandlerAggregate aggregate = mock(StatusHandlerAggregate.class);
        InMemoryStatusItemTranslator  translator = new InMemoryStatusItemTranslator (top, aggregate, db, file);
        translator.start();

        //map translation
        List<StatusItem<?>> translated = translator.translate(new BasicStatus<Object>(top.buildStatusItemName("original1"), 0));
        translated.addAll(translator.translate(new BasicStatus<Object>(top.buildStatusItemName("original2"), 0)));
        assertEquals(2,translated.size());

        StatusItem first =new HealthStatus(top.buildStatusItemName("target"), Health.GOOD);
        assertEquals(first.getName(),translated.get(0).getName());
        assertEquals(first.getValue(),translated.get(0).getValue());

        StatusItem second =new HealthStatus(top.buildStatusItemName("target"), Health.BAD);
        assertEquals(second.getName(),translated.get(1).getName());
        assertEquals(second.getValue(),translated.get(1).getValue());
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
    public void testTranslationTwiceUpdate() throws JAXBException, IOException, JMSException, SAXException {
        Top top = new TopImpl("gpi", "gpi");
        String file = getClass().getResource("status-translator.xml").getFile();
        StatusDatabaseService db = new StatusDatabase();
        StatusHandlerAggregate aggregate = mock(StatusHandlerAggregate.class);
        InMemoryStatusItemTranslator  translator = new InMemoryStatusItemTranslator (top, aggregate, db, file);
        translator.start();

        translator.update(new BasicStatus<Object>(top.buildStatusItemName("twice"), 0));
        verify(aggregate,times(2)).update(any(StatusItem.class));
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
