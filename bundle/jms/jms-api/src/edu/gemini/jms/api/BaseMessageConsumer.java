package edu.gemini.jms.api;

import javax.jms.*;

/**
 * A JMS Message Consumer, which can be defined to process different
 * types of messages.
 */

public class BaseMessageConsumer extends BaseJmsArtifact {

    private MessageConsumer _consumer;

    private MessageListener _listener;


    public BaseMessageConsumer(String clientName, DestinationData data) {
        super(data, clientName);
    }

    public BaseMessageConsumer(String clientName, DestinationData data, MessageListener listener) {
        super(data, clientName);
        _listener = listener;
    }

    /**
     * Set the listener this consumer will invoke. This method must be called
     * before the invocation of startJms();
     * The listener will replace any listener during construction.
     * @param listener listener to use by this consumer
     */
    public void setMessageListener(MessageListener listener) {
        _listener = listener;
    }

    protected void constructJmsObject(Destination destination) throws JMSException {
        if (destination != null) {
            _consumer = _session.createConsumer(destination);
            _consumer.setMessageListener(_listener);
        } else {
            LOG.warning("Problem setting consumer for Destination: " + destination);
        }
    }

    protected void destroyJmsObject() throws JMSException {
        if (_consumer != null)
            _consumer.close();
    }
}
