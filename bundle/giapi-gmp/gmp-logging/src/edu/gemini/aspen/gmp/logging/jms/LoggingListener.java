package edu.gemini.aspen.gmp.logging.jms;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.logging.LogMessage;
import edu.gemini.aspen.gmp.logging.LogProcessor;

import javax.jms.MessageListener;
import javax.jms.Message;

/**
 * Listener class for the logging messages
 */
public class LoggingListener implements MessageListener {

    /**
     * The destination from where the messages will come from
     */
    public static final String DESTINATION_NAME = JmsKeys.GMP_SERVICES_LOG_DESTINATION;

    /**
     * The log processor
     */
    private final LogProcessor _logProcessor;

    public LoggingListener(LogProcessor logProcessor) {
        if (logProcessor == null) throw new IllegalArgumentException("Can't initialize a Logging Listener with a null Log Processor"); 
        _logProcessor = logProcessor;
    }

    public void onMessage(Message message) {
        LogMessage msg = new JmsLogMessage(message);
        _logProcessor.processLogMessage(msg);
    }
}
