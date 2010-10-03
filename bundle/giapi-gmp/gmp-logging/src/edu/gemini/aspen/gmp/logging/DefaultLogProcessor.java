package edu.gemini.aspen.gmp.logging;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The Default Log Processor simply takes the message and uses
 * the standard java logging infrastructure to log the information
 * using the appropriate log level.
 */
public class DefaultLogProcessor implements LogProcessor {

    static final Logger LOG = Logger.getLogger(DefaultLogProcessor.class.getName());

    //Store the last message received. Used mainly for testing purposes
    private LogMessage _lastMessage;

    public void processLogMessage(LogMessage msg) {

        if (msg == null || msg.getSeverity() == null) return; //don't do anything if the message or severity is null

        switch (msg.getSeverity()) {
            case INFO:
                LOG.info(msg.getMessage());
                break;
            case SEVERE:
                LOG.log(Level.SEVERE, msg.getMessage());
                break;
            case WARNING:
                LOG.warning(msg.getMessage());
                break;
        }
        _lastMessage = msg;              
    }

    /**
     * Returns the last message received. This is used for unit testing purposes only
     * @return The last message received.
     */
    LogMessage getLastMessage() {
        return _lastMessage;
    }
}
