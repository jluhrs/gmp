package edu.gemini.aspen.gmp.statusgw.jms;


import edu.gemini.aspen.gmp.status.api.StatusDatabaseService;
import edu.gemini.aspen.gmp.status.api.StatusItem;
import edu.gemini.aspen.gmp.util.jms.GmpJmsUtil;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The Status Request Processor receives a request via JMS about a Status
 * Item by name. The Status Request Processor looks for the status item in
 * the database service it contains and will send a reply back to the client
 * with the information about it. 
 */
public class StatusRequestProcessor implements ExceptionListener, MessageListener {

    private static final Logger LOG = Logger.getLogger(StatusRequestProcessor.class.getName());

    private StatusDatabaseService _db;

    private Connection _connection;
    private Session _session;
    private MessageConsumer _consumer;
    private MessageProducer _replyProducer;


    public StatusRequestProcessor(StatusDatabaseService db, JmsProvider provider) {
        _db = db;

        try {
            ConnectionFactory factory = provider.getConnectionFactory();
            _connection = factory.createConnection();
            _connection.setClientID("Gateway Status Consumer");
            _connection.start();
            _connection.setExceptionListener(this);
            _session = _connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            //Commands come from a predefined topic
            Destination destination = _session.createTopic(
                    GmpKeys.GW_STATUS_REQUEST_DESTINATION);
            _consumer = _session.createConsumer(destination);
            _consumer.setMessageListener(this);

            _replyProducer = _session.createProducer(null);
            LOG.info("Status Gateway Consumer Started");
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem initializing the status request consumer ", e);
        }
    }

    public void stop() {

        try {
            _consumer.close();
            _session.close();
            _connection.close();
            _replyProducer.close();
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem stopping status request consumer", e);
        }
        LOG.info("Status Gateway stopped");
    }

    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on status request consumer: ", e);
    }

    public void onMessage(Message message) {

        try {
            String statusName = message.getStringProperty(GmpKeys.GW_STATUS_NAME_PROPERTY);
            StatusItem item = _db.getStatusItem(statusName);

            Message replyMessage = GmpJmsUtil.buildStatusItemMessage(_session, item);
            _replyProducer.send(message.getJMSReplyTo(), replyMessage);

        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem processing status item request message: ", e);
        }

    }
}
