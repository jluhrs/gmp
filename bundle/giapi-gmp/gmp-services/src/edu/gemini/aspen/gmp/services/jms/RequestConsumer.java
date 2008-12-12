package edu.gemini.aspen.gmp.services.jms;

import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;
import edu.gemini.aspen.gmp.services.properties.PropertyService;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 */
public class RequestConsumer implements MessageListener, ExceptionListener {

    private static final Logger LOG = Logger.getLogger(RequestConsumer.class.getName());

    private Connection _connection;
    private Session _session;
    private MessageConsumer _consumer;

    private PropertyService _propertyService;


    public RequestConsumer(JmsProvider provider) {

        ConnectionFactory factory = provider.getConnectionFactory();
        try {
            _connection = factory.createConnection();
            _connection.setClientID("Service Request Consumer");
            _connection.start();
            _connection.setExceptionListener(this);
            _session = _connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            //Completion info comes from a queue
            Destination destination = _session.createQueue(
                    GmpKeys.GMP_UTIL_REQUEST_DESTINATION);
            _consumer = _session.createConsumer(destination);
            _consumer.setMessageListener(this);

            _propertyService = new PropertyService();
            LOG.info(
                    "Message Consumer started to receive service requests");
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception starting up Service Request Consumer", e);
        }
    }


    public void close() {
        try {
            _consumer.close();
            _session.close();
            _connection.close();
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception closing Service Request Consumer: ", e);
        }
    }

    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on Services Request Consumer", e);
    }

    public void onMessage(Message message) {
        try {
            if (message instanceof MapMessage) {
                MapMessage mm = (MapMessage) message;

                int requestType = mm.getIntProperty(GmpKeys.GMP_UTIL_REQUEST_TYPE);

                switch (requestType) {
                    case GmpKeys.GMP_UTIL_REQUEST_PROPERTY:
                        String key = mm.getString(GmpKeys.GMP_UTIL_PROPERTY);
                        
                        String reply = _propertyService.getProperty(key);

                        Destination destination = message.getJMSReplyTo();
                        if (destination == null) {
                            LOG.info("Invalid destination received. Can't reply to request");
                            return;
                        }

                        MessageProducer replyProducer = _session.createProducer(destination);

                        Message replyMessage = _session.createTextMessage(reply);

                        replyProducer.send(replyMessage);
                        break;

                    default:
                        LOG.info("Invalid request received: " + requestType);
                }
            } else {
                LOG.info("Unexpected message received by Services Request Consumer");
            }
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception receiving Service Request", e);
        }
    }
}
