package edu.gemini.jms.api;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TemporaryQueue;

/**
 * Base class to model a request/reply communication using JMS.
 * <br>
 * Implementations of this class need to define how to reconstruct the
 * reply message in the communication as an Object with the method buildResponse
 * <br>
 * Also they need to customize what kind of consumer will be used to get the reply
 * overriding the method createReplyConsumer. For example the reply could be a temporary
 * queue or a fixed queue with a correlationID selector
 */
public abstract class JmsMapMessageSenderReply<T> extends JmsMapMessageSender
        implements MapMessageSenderReply<T> {

    public JmsMapMessageSenderReply(String clientName) {
        super(clientName);
    }

    @Override
    public T sendMessageWithReply(DestinationData destination,
                                  MapMessageBuilder messageBuilder,
                                  long timeout) throws MessagingException {
        long startTime = System.currentTimeMillis();
        Message m = sendMapMessageWithReply(destination, messageBuilder);
        T replyObject = waitForReply(m, timeout);
        long endTime = System.currentTimeMillis();
        LOG.fine("Sending/reply on " + destination + " took " + (endTime - startTime) + "   [ms]");
        return replyObject;

    }

    private MapMessage sendMapMessageWithReply(DestinationData destinationData, MapMessageBuilder messageBuilder) throws MessagingException {
        MapMessage mm;
        try {
            Destination destination = createDestination(destinationData);
            mm = MapMessageCreator.ReplyCreator.createMapMessage(_session);

            messageBuilder.constructMessageBody(mm);

            _producer.send(destination, mm);
        } catch (JMSException e) {
            throw new MessagingException("Unable to send message", e);
        }
        return mm;
    }

    private T waitForReply(Message requestMessage, long timeout) {
        try {
            return waitForReplyMessage(requestMessage, timeout);
        } catch (JMSException e) {
            throw new MessagingException("Problem receiving reply", e);
        }
    }

    protected T waitForReplyMessage(Message requestMessage, long timeout) throws JMSException {
        MessageConsumer tempConsumer = createReplyConsumer(requestMessage);
        Message reply = tempConsumer.receive(timeout);
        tempConsumer.close();

        if (requestMessage.getJMSReplyTo() instanceof TemporaryQueue) {
            TemporaryQueue temporaryQueue = (TemporaryQueue) requestMessage.getJMSReplyTo();
            temporaryQueue.delete();
        }
        if (reply == null) {
            LOG.severe("Reply awaited on " + tempConsumer + " is null, probably the response timed-out after " + timeout + " [ms]");
            //throw new JMSException("Expected reply for a request");
        }
        return buildResponse(reply);
    }

    /**
     * Creates a consumer that is able to accept replies to the request message
     *
     * @param requestMessage
     * @return a Message Consumer listening for replies to the request message
     * @throws JMSException
     */
    protected abstract MessageConsumer createReplyConsumer(Message requestMessage) throws JMSException;

    /**
     * Reconstruct the reply object from the reply message
     *
     * @param reply message representing the answer to the request
     * @return object representing the reply.
     * @throws JMSException if there is a problem decoding the
     *                      reply
     */
    protected abstract T buildResponse(Message reply) throws JMSException;
}
