package edu.gemini.jms.api;

/**
 * Interface JmsMessageSelector to be used as selector in Message Consumers
 * It is a String with an SQL like query to filter messages according to property values
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 27, 2010
 */
public interface JmsMessageSelector {
    /**
     * Get the selector String.
     *
     * @return the selector String to be passed to the createConsumer method.
     */
    String getSelectorString();
}
