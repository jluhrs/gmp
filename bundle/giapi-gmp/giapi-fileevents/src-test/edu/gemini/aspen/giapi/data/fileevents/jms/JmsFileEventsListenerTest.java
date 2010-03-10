package edu.gemini.aspen.giapi.data.fileevents.jms;

import edu.gemini.aspen.giapi.data.fileevents.FileEventAction;
import edu.gemini.aspen.giapi.data.fileevents.FileEventException;
import edu.gemini.aspen.gmp.data.Dataset;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQMessage;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit test class for the File Event Listener
 */
public class JmsFileEventsListenerTest {


    private JmsFileEventsListener _listener;
    private TestAction _action;

    int _intermediateCount = 0;
    int _ancillaryCount = 0;

    /**
     * A test action class. 
     */
    private class TestAction implements FileEventAction {

        private String filename = null;
        private Dataset dataset = null;
        private String hint = null;

        public void onAncillaryFileEvent(String filename, Dataset dataset) {
            this.filename = filename;
            this.dataset = dataset;
            _ancillaryCount++;
        }

        public void onIntermediateFileEvent(String filename, Dataset dataset, String hint) {
            this.filename = filename;
            this.dataset = dataset;
            this.hint = hint;
            _intermediateCount++;
        }

        public String getFilename() {
            return filename;
        }

        public Dataset getDataset() {
            return dataset;
        }

        public String getHint() {
            return hint;
        }
    }


    @Before
    public void setUp() {
        _action = new TestAction();
        _listener = new JmsFileEventsListener(
                _action
        );
        _ancillaryCount = _intermediateCount = 0;

    }

    @After
    public void tearDown() {
        _listener = null;
        _action = null;
    }

    @Test
    public void testGoodAncillaryMessage() {
        MapMessage message = new ActiveMQMapMessage();
        try {
            //type set to Ancillary Files
            message.setIntProperty(GmpKeys.GMP_DATA_FILEEVENT_TYPE, 0);
            message.setString(GmpKeys.GMP_DATA_FILEEVENT_DATALABEL, "datalabel-1");
            message.setString(GmpKeys.GMP_DATA_FILEEVENT_FILENAME, "filename-1");

        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }

        _listener.onMessage(message);

        //confirm the handler got invoked
        assertEquals(1, _ancillaryCount);
        assertEquals(0, _intermediateCount);
        assertEquals("datalabel-1", _action.getDataset().getName());
        assertEquals("filename-1", _action.getFilename());
        assertNull(_action.getHint());
    }

    @Test
    public void testGoodIntermediateMessageNoHint() {
        MapMessage message = new ActiveMQMapMessage();
        try {
            //type set to Intermediate Files
            message.setIntProperty(GmpKeys.GMP_DATA_FILEEVENT_TYPE, 1);
            message.setString(GmpKeys.GMP_DATA_FILEEVENT_DATALABEL, "datalabel-2");
            message.setString(GmpKeys.GMP_DATA_FILEEVENT_FILENAME, "filename-2");

        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }

        _listener.onMessage(message);

        //confirm the handler got invoked
        assertEquals(0, _ancillaryCount);
        assertEquals(1, _intermediateCount);
        assertEquals("datalabel-2", _action.getDataset().getName());
        assertEquals("filename-2", _action.getFilename());
        assertNull(_action.getHint());
    }


    @Test
    public void testGoodIntermediateMessageWithHint() {
        MapMessage message = new ActiveMQMapMessage();
        try {
            //type set to Intermediate Files
            message.setIntProperty(GmpKeys.GMP_DATA_FILEEVENT_TYPE, 1);
            message.setString(GmpKeys.GMP_DATA_FILEEVENT_DATALABEL, "datalabel-3");
            message.setString(GmpKeys.GMP_DATA_FILEEVENT_FILENAME, "filename-3");
            message.setString(GmpKeys.GMP_DATA_FILEEVENT_HINT, "hint-1");

        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }

        _listener.onMessage(message);

        //confirm the handler got invoked
        assertEquals(0, _ancillaryCount);
        assertEquals(1, _intermediateCount);
        assertEquals("datalabel-3", _action.getDataset().getName());
        assertEquals("filename-3", _action.getFilename());
        assertEquals("hint-1", _action.getHint());
    }

    @Test
            (expected = FileEventException.class)
    public void testIncompleteAncillaryMessageNoFilename() {
        MapMessage message = new ActiveMQMapMessage();
        try {
            //type set to Ancillary Files
            message.setIntProperty(GmpKeys.GMP_DATA_FILEEVENT_TYPE, 0);
            message.setString(GmpKeys.GMP_DATA_FILEEVENT_DATALABEL, "datalabel-4");

        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }
        //this should throw a FileEventException
        _listener.onMessage(message);
    }

    @Test
            (expected = FileEventException.class)
    public void testIncompleteAncillaryMessageNoDataset() {
        MapMessage message = new ActiveMQMapMessage();
        try {
            //type set to Ancillary Files
            message.setIntProperty(GmpKeys.GMP_DATA_FILEEVENT_TYPE, 0);

        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }
        //this should throw a FileEventException
        _listener.onMessage(message);
    }


    @Test
            (expected = FileEventException.class)
    public void testInvalidFileEventMessageWrongType() {
        MapMessage message = new ActiveMQMapMessage();
        try {
            //type set to a wrong type
            message.setIntProperty(GmpKeys.GMP_DATA_FILEEVENT_TYPE, 3);
            message.setString(GmpKeys.GMP_DATA_FILEEVENT_DATALABEL, "datalabel-1");
            message.setString(GmpKeys.GMP_DATA_FILEEVENT_FILENAME, "filename-1");

        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }
        //this should throw an exception
        _listener.onMessage(message);
    }

    @Test
            (expected = FileEventException.class)
    public void testInvalidFileEventMessageWrongJmsMessage() {
        Message message = new ActiveMQMessage();
        try {
            //type set to Intermediate Files
            message.setIntProperty(GmpKeys.GMP_DATA_FILEEVENT_TYPE, 1);

        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }
        //this should throw an exception
        _listener.onMessage(message);
    }


}
