package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import javax.jms.JMSException;

/**
 * Exception thrown when a message doesn't follow the agreed protocol
 */
public class FormatException extends RuntimeException {
    public FormatException(String msg) {
        super(msg);
    }

    public FormatException(String msg, JMSException e) {
        super(msg, e);
    }
}
