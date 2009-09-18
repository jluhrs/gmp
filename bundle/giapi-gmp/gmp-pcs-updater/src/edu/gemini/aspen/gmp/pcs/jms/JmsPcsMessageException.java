package edu.gemini.aspen.gmp.pcs.jms;

/**
 * Runtime exceptions for JMS Messaging processing.
 * This is useful for unit testing.
 */
public class JmsPcsMessageException extends RuntimeException{

    public JmsPcsMessageException(String message, Exception cause) {
        super(message, cause);
    }

    public JmsPcsMessageException(String message) {
        super(message);
    }
}
