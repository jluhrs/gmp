package edu.gemini.aspen.gmp.logging.jms;

import org.junit.Test;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.ActiveMQBytesMessage;
import static org.junit.Assert.*;


import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.jms.Message;

import edu.gemini.aspen.gmp.logging.LoggingException;
import edu.gemini.aspen.gmp.logging.Severity;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;

/**
 * Test class for the JmsLogMessage
 */
public class JmsLogMessageTest {

    private static final String LOG_MESSAGE_EXAMPLE = "This is an example log message";

    @Test
    public void testConstruction() {


        try {
            TextMessage tm = new ActiveMQTextMessage();
            tm.setIntProperty(JmsKeys.GMP_SERVICES_LOG_LEVEL, 1);
            JmsLogMessage logMessage = new JmsLogMessage(tm);
            assertEquals(Severity.INFO, logMessage.getSeverity());
            assertNull(logMessage.getMessage());

            //add a message
            tm.setText(LOG_MESSAGE_EXAMPLE);
            logMessage = new JmsLogMessage(tm);
            assertEquals(Severity.INFO, logMessage.getSeverity());
            assertEquals(LOG_MESSAGE_EXAMPLE, logMessage.getMessage());

            //a warning message
            tm = new ActiveMQTextMessage();
            tm.setIntProperty(JmsKeys.GMP_SERVICES_LOG_LEVEL, 2);
            tm.setText(LOG_MESSAGE_EXAMPLE);
            logMessage = new JmsLogMessage(tm);
            assertEquals(Severity.WARNING, logMessage.getSeverity());
            assertEquals(LOG_MESSAGE_EXAMPLE, logMessage.getMessage());

            //a severity message
            tm = new ActiveMQTextMessage();
            tm.setIntProperty(JmsKeys.GMP_SERVICES_LOG_LEVEL, 3);
            tm.setText(LOG_MESSAGE_EXAMPLE);
            logMessage = new JmsLogMessage(tm);
            assertEquals(Severity.SEVERE, logMessage.getSeverity());
            assertEquals(LOG_MESSAGE_EXAMPLE, logMessage.getMessage());

        } catch (LoggingException e) {
            fail("Unexpected Logging Exception while parsing log message");
        } catch (JMSException e) {
            fail("Unexpected JMS Exception while constructing log message");
        }

    }

    @Test
    (expected = LoggingException.class)
    public void testConstructionWithInvalidMessageType() throws LoggingException {
        Message tm = new ActiveMQBytesMessage();
        try {
            tm.setIntProperty(JmsKeys.GMP_SERVICES_LOG_LEVEL, 1);
            new JmsLogMessage(tm);
        } catch (JMSException e) {
            fail("Unexpected JMS Exception while constructing log message");
        }
    }


    @Test
    (expected = LoggingException.class)
    public void testConstructionWithInvalidMessageContent() throws LoggingException {
        Message tm = new ActiveMQTextMessage();
        try {
            tm.setIntProperty(JmsKeys.GMP_SERVICES_LOG_LEVEL, 0);
            new JmsLogMessage(tm);
        } catch (JMSException e) {
            fail("Unexpected JMS Exception while constructing log message");
        }
    }




}
