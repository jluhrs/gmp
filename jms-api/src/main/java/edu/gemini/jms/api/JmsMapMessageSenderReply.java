package edu.gemini.jms.api;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.JMSException;
import java.util.Map;

/**
 * Base class to model a request/reply communication using JMS.
 * Implementations of this class need to define how to reconstruct the
 * reply message in the communication as an Object.
 */
public abstract class JmsMapMessageSenderReply<T> extends JmsMapMessageSender
        implements MapMessageSenderReply {

    public JmsMapMessageSenderReply(String clientData) {
        super(clientData);
    }

    public T sendMapMessageReply(DestinationData destination,
                                      Map<String, Object> message,
                                      Map<String, Object> properties,
                                      long timeout) throws MessagingException {

        Message m = sendMapMessageWithCreator(destination,
                message,
                properties,
                MapMessageCreator.ReplyCreator);

        MessageConsumer tempConsumer;
        try {
            tempConsumer = _session.createConsumer(m.getJMSReplyTo());
            Message reply = tempConsumer.receive(timeout);
            tempConsumer.close();
            return buildResponse(reply);

        } catch (JMSException e) {
            throw new MessagingException("Problem receiving reply", e);
        }

    }

    /**
     * Reconstruct the reply object from the reply message
     * @param reply message representing the answer to the request
     * @return object representing the reply.
     *
     * @throws JMSException if there is a problem decoding the
     * reply
     */
    protected abstract T buildResponse(Message reply) throws JMSException;
}
