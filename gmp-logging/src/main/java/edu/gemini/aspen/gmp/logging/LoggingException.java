package edu.gemini.aspen.gmp.logging;

/**
 * Logging Exception class
 */
public class LoggingException extends RuntimeException {

    public LoggingException(String message, Exception cause) {
        super(message, cause);
    }

    public LoggingException(String message) {
        super(message);
    }
}
