package edu.gemini.aspen.gmp.logging.jms;

import edu.gemini.aspen.gmp.util.jms.GmpKeys;
import edu.gemini.aspen.gmp.logging.LogMessage;
import edu.gemini.aspen.gmp.logging.LogProcessor;
import edu.gemini.aspen.gmp.logging.LoggingException;

import javax.jms.MessageListener;
import javax.jms.Message;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Listener class for the logging messages
 */
public class LoggingListener implements MessageListener {

    private static final Logger LOG = Logger.getLogger(LoggingListener.class.getName());
    /**
     * The destination from where the messages will come from
     */
    public static final String DESTINATION_NAME = GmpKeys.GMP_SERVICES_LOG_DESTINATION;

    /**
     * The log processor
     */
    private final LogProcessor _logProcessor;

    public LoggingListener(LogProcessor logProcessor) {
        _logProcessor = logProcessor;
    }

    public void onMessage(Message message) {

        try {
            //convert the message into a LogMessage.
            LogMessage msg = new JmsLogMessage(message);
            _logProcessor.processLogMessage(msg);
        } catch (LoggingException e) {
            LOG.log(Level.WARNING, "Problem processing log message", e);
        }

    }
}
