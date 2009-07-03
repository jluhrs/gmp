package edu.gemini.aspen.giapi.data.obsevents.jms;

import org.junit.Before;
import org.junit.After;
import org.apache.activemq.command.ActiveMQMessage;

import edu.gemini.aspen.giapi.data.obsevents.jms.JmsObservationEventMonitor;
import edu.gemini.aspen.giapi.data.obsevents.ObservationEventMonitorTestBase;
import edu.gemini.aspen.giapi.data.obsevents.ObservationEventMonitor;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;

import javax.jms.Message;
import javax.jms.JMSException;

/**
 * Test class for the JMS implementation of the Observation Event Monitor.
 */
public class JmsObservationEventMonitorTest extends ObservationEventMonitorTestBase {


    JmsObservationEventMonitor monitor;

    Message message;

    @Before
    public void setUp() {
        monitor = new JmsObservationEventMonitor();
    }

    @After
    public void tearDown() {
        monitor = null;
    }


    public ObservationEventMonitor getMonitor() {
        return monitor;
    }

    /**
     * We will put a JMS message with the right properties in the
     * onMessage() method. 
     */
    public void triggerHandler() {

        message = new ActiveMQMessage();
        try {
            message.setStringProperty(GmpKeys.GMP_DATA_OBSEVENT_NAME, "OBS_START_ACQ");
            message.setStringProperty(GmpKeys.GMP_DATA_OBSEVENT_FILENAME, "S20090100101");
        } catch (JMSException e) {
            org.junit.Assert.fail("Exception setting GMP Data Properties into JMS Message");
        }
        monitor.onMessage(message);
    }




}
