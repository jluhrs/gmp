package edu.gemini.jms.api;

import javax.jms.*;

/**
 * A JMS Message Producer, which can be defined to send different
 * types of messages. This class is usually extended to send specific
 * types of messages
 */

public class BaseMessageProducer extends BaseJmsArtifact {


    protected MessageProducer _producer;

    public BaseMessageProducer(String clientName, DestinationData data) {
        super(data, clientName);
    }

    /**
     * Creates the actual producer. If the destination is <code>null</code>,
     * an anonymous producer is created.
     * @param d Destination to be used by the consumer or producer.
     * @throws JMSException
     */
    protected void constructJmsObject(Destination d) throws JMSException {
        _producer = _session.createProducer(d);
    }

    protected void destroyJmsObject() throws JMSException {
        if (_producer != null) {
            _producer.close();
        }
    }


}
