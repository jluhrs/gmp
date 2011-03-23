package edu.gemini.jms.api;

import com.google.common.collect.Maps;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Implementation of a {@link edu.gemini.jms.api.MapMessageSender} using JMS.
 */
public class JmsMapMessageSender extends BaseMessageProducer implements MapMessageSender {
    private DestinationBuilder _destinationBuilder;
    private ConcurrentMap<String, Destination> _destinationCache;

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
                TemporaryQueue temporaryQueue = session.createTemporaryQueue();
                m.setJMSReplyTo(temporaryQueue);
                return m;
            }
        };

        abstract MapMessage createMapMessage(Session session) throws JMSException;
    }

    public JmsMapMessageSender(String clientName) {
        super(clientName, null);
        _destinationBuilder = new DestinationBuilder();
        _destinationCache = Maps.newConcurrentMap();
    }

    @Override
    public MapMessage sendMapMessage(DestinationData destinationData, MapMessageBuilder messageBuilder) throws MessagingException {
        if (isConnected()) {
            return sendMapMessage(destinationData, messageBuilder, MapMessageCreator.NoReplyCreator);
        } else {
            throw new MessagingException("Attempt to send a message when the sender is not ready");
        }
    }

    private MapMessage sendMapMessage(DestinationData destinationData, MapMessageBuilder messageBuilder,
                                      MapMessageCreator creator) throws MessagingException {
        Destination destination;
        try {
            destination = createDestination(destinationData);
            return doMessageSending(destination, messageBuilder, creator);
        } catch (JMSException e) {
            throw new MessagingException("Unable to send message to destination " + destinationData, e);
        }
    }

    private MapMessage doMessageSending(Destination destination, MapMessageBuilder messageBuilder, MapMessageCreator creator) throws JMSException {
        MapMessage mm = creator.createMapMessage(_session);

        // Delegate the construction of the map message
        messageBuilder.constructMessageBody(mm);
        _producer.send(destination, mm);

        return mm;
    }

    protected Destination createDestination(DestinationData destination) throws JMSException {
        Destination d = _destinationCache.get(destination.getName());
        if (d == null) {
            d = _destinationBuilder.newDestination(destination, _session);
            _destinationCache.put(destination.getName(), d);
        }
        return d;
    }
}
