package edu.gemini.jms.api;

import javax.jms.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Implementation of a {@link edu.gemini.jms.api.MapMessageSender} using JMS.
 */
public class JmsMapMessageSender extends BaseMessageProducer implements MapMessageSender {

    private MessageBuilder _messageBuilder;
    private DestinationBuilder _destinationBuilder;

    private Map<String, Destination> _destinationCache;


    /**
     * Utility types to create messages.
     */
    protected enum MapMessageCreator {

        NoReplyCreator {
            public MapMessage createMapMessage(Session session) throws JMSException {
                return session.createMapMessage();
            }
        },

        ReplyCreator {
            public MapMessage createMapMessage(Session session) throws JMSException {
                MapMessage m = session.createMapMessage();
                m.setJMSReplyTo(session.createTemporaryQueue());
                return m;
            }
        };

        abstract MapMessage createMapMessage(Session session) throws JMSException;
    }

    public JmsMapMessageSender(String clientData) {
        super(clientData, null);
        _messageBuilder = new MessageBuilder();
        _destinationBuilder = new DestinationBuilder();
        _destinationCache = new HashMap<String, Destination>();

    }

    public void sendMapMessage(DestinationData destination,
                               Map<String, Object> message,
                               Map<String, Object> properties) throws MessagingException {
        sendMapMessageWithCreator(destination,
                message,
                properties,
                MapMessageCreator.NoReplyCreator);
    }


    /**
     * Auxiliary method to send a Map message using the specified
     * mapMessageCreator. This method uses the destingation data object
     * to construct the actual JMS destination where the message should
     * be sent. The destinations are cached so there is no need to
     * reconstruct them every time
     * @param destination where to send the message
     * @param message the message content
     * @param properties message properties
     * @param mapMessageCreator defines how to construct the message.
     *
     * @return the Message that was sent, in case it is required for
     * the caller code. Usually this is needed for request-reply communications.
     */
    protected Message sendMapMessageWithCreator(DestinationData destination,
                                              Map<String, Object> message,
                                              Map<String, Object> properties,
                                              MapMessageCreator mapMessageCreator) {
        if (!isConnected()) return null; //don't do anything if there is no connection

        try {

            Destination d = _destinationCache.get(destination.getName());
            if (d == null) {
                d = _destinationBuilder.newDestination(destination, _session);
                _destinationCache.put(destination.getName(), d);
            }

            return sendMapMessage(d, message, properties, mapMessageCreator);
        } catch (JMSException e) {
            throw new MessagingException("Unable to send message", e);
        }
    }


    protected MapMessage sendMapMessage(Destination destination,
                                        Map<String, Object> message,
                                        Map<String, Object> properties,
                                        MapMessageCreator creator) throws MessagingException {
        MapMessage mm;
        try {
            mm = creator.createMapMessage(_session);

            _messageBuilder.buildMapMessage(mm, message);

            _messageBuilder.setMessageProperties(mm, properties);

            _producer.send(destination, mm);

        } catch (JMSException e) {
            throw new MessagingException("Unable to send message", e);
        }
        return mm;
    }


}
