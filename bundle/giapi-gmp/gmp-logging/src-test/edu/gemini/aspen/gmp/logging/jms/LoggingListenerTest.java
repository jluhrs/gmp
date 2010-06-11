package edu.gemini.aspen.gmp.logging.jms;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.fail;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.ActiveMQBytesMessage;
import edu.gemini.aspen.gmp.logging.TestLogProcessor;

import static org.junit.Assert.*;


import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.jms.Message;

/**
 *
 */
public class LoggingListenerTest {

    private LoggingListener _listener;
    private final TestLogProcessor _processor = new TestLogProcessor();

    @Before
    public void setUp() {
        _processor.reset();
        _listener = new LoggingListener(_processor);
    }

    @Test
    public void invokedListener() {

        TextMessage tm = new ActiveMQTextMessage();
        try {
            tm.setIntProperty(JmsKeys.GMP_SERVICES_LOG_LEVEL, 1);
        } catch (JMSException e) {
            fail("Unexpected exception");
        }

        _listener.onMessage(tm);
        //check that the processor was invoked
        assertTrue(_processor.wasInvoked());

    }


    @Test
    public void notInvokedListenerWrongSeverity() {

        TextMessage tm = new ActiveMQTextMessage();
        try {
            tm.setIntProperty(JmsKeys.GMP_SERVICES_LOG_LEVEL, 0);
        } catch (JMSException e) {
            fail("Unexpected exception");
        }

        _listener.onMessage(tm);
        //Confirms the processor was not called.
        assertFalse(_processor.wasInvoked());

    }


    @Test
    public void notInvokedListenerWrongMessage() {

        Message tm = new ActiveMQBytesMessage();
        try {
            tm.setIntProperty(JmsKeys.GMP_SERVICES_LOG_LEVEL, 0);
        } catch (JMSException e) {
            fail("Unexpected exception");
        }

        _listener.onMessage(tm);
        //Confirms the processor was not called.
        assertFalse(_processor.wasInvoked());

    }

}
