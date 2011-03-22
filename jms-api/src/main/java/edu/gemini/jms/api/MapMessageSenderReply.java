package edu.gemini.jms.api;

/**
 * An extension to the {@link edu.gemini.jms.api.MapMessageSender} interface
 * to provide a request-reply communication.
 */
public interface MapMessageSenderReply<T> extends MapMessageSender {

    /**
     * Use a request/reply model to send a message to the given
     * destination. The message  can be decorated using the messageBuilder
     *
     * @param destination where the message will be send to
     * @param messageBuilder     An object capable of building a message
     * @param timeout     time (in milliseconds) to wait for a response.
     * @return an Object of type T representing the reply to the given message.
     * @throws MessagingException in case there is a problem sending the message
     */
    public T sendMessageWithReply(DestinationData destination,
                                  MapMessageBuilder messageBuilder,
                                  long timeout) throws MessagingException;
}


