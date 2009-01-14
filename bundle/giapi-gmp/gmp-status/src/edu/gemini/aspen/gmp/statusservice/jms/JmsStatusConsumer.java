package edu.gemini.aspen.gmp.statusservice.jms;

import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;
import edu.gemini.aspen.gmp.util.jms.GmpJmsUtil;
import edu.gemini.aspen.gmp.status.api.StatusItem;
import edu.gemini.aspen.gmp.statusservice.core.StatusUpdater;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *  The JMS Consumer to receive Status Items from the instrument
 */
public class JmsStatusConsumer implements MessageListener, ExceptionListener {

    private static final Logger LOG = Logger.getLogger(JmsStatusConsumer.class.getName());

    private Connection _connection;
    private Session _session;
    private MessageConsumer _consumer;

    private StatusUpdater _updater;
    
    public JmsStatusConsumer(JmsProvider provider, StatusUpdater updater) {

        ConnectionFactory factory = provider.getConnectionFactory();
        _updater = updater;

         try {
            _connection = factory.createConnection();
            _connection.setClientID("JMS Status Consumer");
            _connection.start();
            _connection.setExceptionListener(this);
            _session = _connection.createSession(false,
                                                 Session.AUTO_ACKNOWLEDGE);
            //Status will come from a topic
            Destination destination = _session.createTopic(
                    GmpKeys.GMP_STATUS_DESTINATION);
            _consumer = _session.createConsumer(destination);
            _consumer.setMessageListener(this);
            LOG.info(
                    "Message Listener started to receive status information");
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem starting up status consumer ", e);
        }
    }


    public void onMessage(Message message) {
        try {
            //reconstruct the StatusItem from the JMS Message
            StatusItem item = GmpJmsUtil.buildStatusItem(message);
            if (item != null) {
                _updater.update(item);
            }

        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem receiving status message", e);
        }
    }

    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on Status Consumer", e);
    }

    public void close() {
        try {
            _consumer.close();
            _session.close();
            _connection.close();
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem closing Status Consumer", e);
        }
    }
}
