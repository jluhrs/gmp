package edu.gemini.jms.api;

/**
 * Exception thrown when a message doesn't follow the agreed protocol
 */
public class FormatException extends RuntimeException {
    public FormatException(String msg) {
        super(msg);
    }

    public FormatException(String msg, Exception e) {
        super(msg, e);
    }
}
