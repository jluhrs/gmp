package edu.gemini.jms.api;

import javax.jms.*;

/**
 * A JMS Message Consumer, which can be defined to process different
 * types of messages.
 */

public class BaseMessageConsumer extends BaseJmsArtifact {

    private MessageConsumer _consumer;

    private MessageListener _listener;

    private JmsMessageSelector _selector = null;

    public BaseMessageConsumer(String clientName, DestinationData data) {
        super(data, clientName);
    }

    /**
     * Constructor that sets a message listener
     *
     * @param clientName the client ID to pass to the JMS provider
     * @param data       Destination data container used to construct the JMS Destination
     * @param listener   who will process incoming messages
     */
    public BaseMessageConsumer(String clientName, DestinationData data, MessageListener listener) {
        super(data, clientName);
        _listener = listener;
    }

    /**
     * Constructor that sets a message listener and a selector
     *
     * @param clientName      the client ID to pass to the JMS provider
     * @param data            Destination data container used to construct the JMS Destination
     * @param listener        who will process incoming messages
     * @param messageSelector to filter on message property values
     */
    public BaseMessageConsumer(String clientName, DestinationData data, MessageListener listener, JmsMessageSelector messageSelector) {
        super(data, clientName);
        _listener = listener;
        _selector = messageSelector;
    }

    @Override
    public String toString() {
        return "BaseMessageConsumer{" +
                "_consumer=" + _consumer +
                ", _listener=" + _listener +
                ", _selector=" + _selector +
                '}';
    }

    /**
     * Set the listener this consumer will invoke. This method must be called
     * before the invocation of startJms();
     * The listener will replace any listener during construction.
     *
     * @param listener listener to use by this consumer
     */
    public void setMessageListener(MessageListener listener) {
        _listener = listener;
    }

    protected void constructJmsObject(Destination destination) throws JMSException {

        if (destination == null) {
            LOG.warning("No destination specified for JmsObject");
            return;
        }

        if (_selector != null) {
            _consumer = _session.createConsumer(destination, _selector.getSelectorString());
        } else {
            _consumer = _session.createConsumer(destination);
        }
        _consumer.setMessageListener(_listener);
    }

    protected void destroyJmsObject() throws JMSException {
        if (_consumer != null)
            _consumer.close();
    }
}
