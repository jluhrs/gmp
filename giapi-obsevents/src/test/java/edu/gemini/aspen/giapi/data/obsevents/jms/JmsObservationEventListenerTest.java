package edu.gemini.aspen.giapi.data.obsevents.jms;

import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEvent;
import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import org.apache.activemq.command.ActiveMQMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;

import static org.junit.Assert.assertEquals;

/**
 * Test class for the JMS implementation of the Observation Event Monitor.
 */
public class JmsObservationEventListenerTest {


    private int flagValue = 0;

    private final ObservationEventHandler singleHandlerStub = new ObservationEventHandler() {
        public void onObservationEvent(ObservationEvent event, DataLabel dataLabel) {
            ++flagValue;
        }
    };

    private JmsObservationEventListener listener;

    @Before
    public void setUp() {
        listener = new JmsObservationEventListener(singleHandlerStub);
        flagValue = 0;
    }

    @After
    public void tearDown() {
        listener = null;
    }

    /**
     * We will put a JMS message with the right properties in the
     * onMessage() method.
     * @param m The JMS that will be used to trigger this listener
     */
    private void triggerHandler(Message m) {
        listener.onMessage(m);
    }


    @Test
    public void testGoodMessage() {
        Message message = new ActiveMQMessage();
        try {
            message.setStringProperty(JmsKeys.GMP_DATA_OBSEVENT_NAME, "OBS_START_ACQ");
            message.setStringProperty(JmsKeys.GMP_DATA_OBSEVENT_FILENAME, "S20090100101");
        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }

        triggerHandler(message);
        //make sure the handler got invoked
        assertEquals(1, flagValue);
    }

    @Test
    public void testInvalidObseventMessage() {
        Message message = new ActiveMQMessage();
        try {
            message.setStringProperty(JmsKeys.GMP_DATA_OBSEVENT_NAME, "INVALID_OBSEVENT");
            message.setStringProperty(JmsKeys.GMP_DATA_OBSEVENT_FILENAME, "S20090100101");
        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }

        triggerHandler(message);
        //make sure the handler didn't get invoked
        assertEquals(0, flagValue);
    }

    @Test
    public void testNullObseventMessage() {
        Message message = new ActiveMQMessage();
        try {
            message.setStringProperty(JmsKeys.GMP_DATA_OBSEVENT_NAME, null);
            message.setStringProperty(JmsKeys.GMP_DATA_OBSEVENT_FILENAME, "S20090100101");
        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }

        triggerHandler(message);
        //make sure the handler didn't get invoked
        assertEquals(0, flagValue);
    }

    @Test
    public void testNullDatasetMessage() {
        Message message = new ActiveMQMessage();
        try {
            message.setStringProperty(JmsKeys.GMP_DATA_OBSEVENT_NAME, "OBS_START_ACQ");
            message.setStringProperty(JmsKeys.GMP_DATA_OBSEVENT_FILENAME, null);
        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }

        triggerHandler(message);
        //make sure the handler didn't get invoked
        assertEquals(0, flagValue);
    }

    @Test
    public void testEmptyDatasetMessage() {
        Message message = new ActiveMQMessage();
        try {
            message.setStringProperty(JmsKeys.GMP_DATA_OBSEVENT_NAME, "OBS_START_ACQ");
            message.setStringProperty(JmsKeys.GMP_DATA_OBSEVENT_FILENAME, " ");
        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }

        triggerHandler(message);
        //make sure the handler didn't get invoked
        assertEquals(0, flagValue);
    }

}
