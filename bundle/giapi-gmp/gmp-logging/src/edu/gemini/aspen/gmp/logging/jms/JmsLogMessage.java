package edu.gemini.aspen.gmp.logging.jms;

import edu.gemini.aspen.gmp.logging.LogMessage;
import edu.gemini.aspen.gmp.logging.Severity;
import edu.gemini.aspen.gmp.logging.LoggingException;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSException;

/**
 * A log message implementation. Construct a LogMessage based
 * on the content of a JMS message
 */
public class JmsLogMessage implements LogMessage {

    private Severity _severity;

    private String _message;

    public JmsLogMessage(Message message) throws LoggingException {

        if (!(message instanceof TextMessage)) {
            throw new LoggingException("Can't construct a Log Message ");
        }

        TextMessage txtMsg = (TextMessage)message;

        try {
            int severity = txtMsg.getIntProperty(GmpKeys.GMP_SERVICES_LOG_LEVEL);
            _severity = Severity.getSeverityByCode(severity);
            _message = txtMsg.getText();
        } catch (JMSException e) {
            throw new LoggingException("Problem reading logging information from JMS", e);
        }

    }

    public Severity getSeverity() {
        return _severity;
    }

    public String getMessage() {
        return _message;
    }
}
