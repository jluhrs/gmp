package edu.gemini.jms.api;

import java.util.Map;

/**
 * A simplifying interface to send a message (described by a map) and its
 * properties to a given destination.
 */
public interface MapMessageSender {

    /**
     * Send a message described as a Map to the given destination. The message
     * to be sent can be decorated with properties, also described as a map.
     * Notice that both the map message and the properties only accept
     * primitive types for the content.
     *
     * @param destination where the message will be send to
     * @param message     a map describing the content of the message
     * @param properties  properties to be set in the message
     * @throws MessagingException in case there is a problem sending the message
     */
    void sendMapMessage(DestinationData destination,
                        Map<String, Object> message,
                        Map<String, Object> properties)
            throws MessagingException;
}
