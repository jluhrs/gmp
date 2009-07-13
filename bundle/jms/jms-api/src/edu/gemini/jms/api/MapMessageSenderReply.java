package edu.gemini.jms.api;

import java.util.Map;

/**
 * An extension to the {@link edu.gemini.jms.api.MapMessageSender} interface
 * to provide a request-reply communication.
 */
public interface MapMessageSenderReply extends MapMessageSender {

    /**
     * Use a request/reply model to send a message to the given
     * destination. The message  can be decorated with properties,
     * also described as a map.
     * Notice that both the map message and the properties only accept
     * primitive types for the content.
     *
     * @param destination where the message will be send to
     * @param message     a map describing the content of the message
     * @param properties  properties to be set in the message
     * @param timeout     time (in milliseconds) to wait for a response.
     * @return an Object representing the reply to the given message.
     * @throws MessagingException in case there is a problem sending the message
     */
    Object sendMapMessageReply(DestinationData destination,
                               Map<String, Object> message,
                               Map<String, Object> properties,
                               long timeout) throws MessagingException;

}


