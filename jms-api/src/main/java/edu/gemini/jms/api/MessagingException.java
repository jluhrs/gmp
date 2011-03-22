package edu.gemini.jms.api;

/**
 * Exception to be generated when a Messaging problem happens. 
 */
public class MessagingException extends RuntimeException {

    public MessagingException(String message, Exception cause) {
        super(message, cause);
    }

    public MessagingException(String message) {
        super(message);
    }

}
