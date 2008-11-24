package edu.gemini.aspen.gmp.broker.jms;


import javax.jms.*;

import edu.gemini.aspen.gmp.broker.impl.GMPKeys;
import edu.gemini.aspen.gmp.broker.api.GMPService;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.jms.api.JmsProvider;

import java.util.logging.Logger;

/**
 * This is a consumer of Completion Info messages.
 * <p/>
 * Whenever an action in the client code is finished, it has to report back to
 * the GMP with their completion information. Completion information can be
 * either COMPLETED or ERROR.
 */
public class JMSCompletionInfoConsumer implements MessageListener, ExceptionListener {

    private static final Logger LOG = Logger.getLogger(
            JMSCompletionInfoConsumer.class.getName());

    private GMPService _service;
    private Connection _connection;
    private Session _session;
    private MessageConsumer _consumer;

    public JMSCompletionInfoConsumer(GMPService service, JmsProvider provider) {
        _service = service;
        ConnectionFactory connectionFactory = provider.getConnectionFactory();
        try {
            _connection = connectionFactory.createConnection();
            _connection.setClientID("JMS Completion Info Consumer");
            _connection.start();
            _connection.setExceptionListener(this);
            _session = _connection.createSession(false,
                                                       Session.AUTO_ACKNOWLEDGE);
            //Completion info comes from a queue
            Destination destination = _session.createQueue(
                    GMPKeys.GMP_COMPLETION_INFO);
            _consumer = _session.createConsumer(destination);
            _consumer.setMessageListener(this);
            LOG.info(
                    "Message Listener started to receive completion information");
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
    }

    public void onMessage(Message message) {
        try {
            if (message instanceof MapMessage) {
                MapMessage m = (MapMessage) message;

                int actionId = m.getIntProperty(GMPKeys.GMP_ACTIONID_PROP);
                HandlerResponse response = JMSUtil.buildHandlerResponse(m);
                LOG.info(
                        "Received Completion info for action ID " + actionId + " : " + response
                                .toString());
                //Notify the OCS. Based on the action ID, we know who to notify
                _service.updateOcs(actionId, response);
            }
        } catch (JMSException e) {
            LOG.warning("onMessage exception : " + e);
        }
    }

    public void onException(JMSException e) {
        LOG.warning("onException: " + e);
        e.printStackTrace();
    }

    public void close() {
        try {
            _consumer.close();
            _session.close();
            _connection.close();
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
    }
}
