package edu.gemini.jms.api;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * An Abstract JMS Message Consumer, which can be defined to process different
 * types of messages. Every message consumer has its own connection and session
 * to the server, so if you want/need to share connections and sessions among
 * several message consumers, you need manually to create your consumers.
 */

public class BaseMessageConsumer implements ExceptionListener {

    private static final Logger LOG = Logger.getLogger(BaseMessageConsumer.class.getName());


    private Connection _connection;

    private Session _session;

    private MessageConsumer _consumer;

    private MessageListener _listener;

    private String _clientName, _topicName;

    public BaseMessageConsumer(String clientName, String topicName, MessageListener listener) {
        _clientName = clientName;
        _topicName = topicName;
        _listener = listener;
    }


    public void startJms(JmsProvider provider) throws JMSException {

        ConnectionFactory factory = provider.getConnectionFactory();

        _connection = factory.createConnection();
        _connection.setClientID(_clientName);
        _connection.start();
        _connection.setExceptionListener(this);
        _session = _connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);
        //Completion info comes from a topic
        Destination destination = _session.createTopic(_topicName);
        _consumer = _session.createConsumer(destination);
        _consumer.setMessageListener(_listener);


    }

    public void stopJms() {
        try {
            if (_consumer != null)
                _consumer.close();
            if (_session != null)
                _session.close();
            if (_connection != null)
                _connection.close();
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception while stopping Message Consumer ", e);
        }
    }

    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on Message Consumer: ", e);
    }
}
