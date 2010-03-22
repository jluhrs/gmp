package edu.gemini.aspen.gmp.logging;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The Default Log Processor simply takes the message and uses
 * the standard java logging infrastructure to log the information
 * using the appropriate log level.
 */
public class DefaultLogProcessor implements LogProcessor {

    private static final Logger LOG = Logger.getLogger(DefaultLogProcessor.class.getName());

    public void processLogMessage(LogMessage msg) {

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
    }
}
