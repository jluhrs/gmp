package edu.gemini.aspen.gmp.epics.jms;

import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.epics.EpicsConfiguration;

import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A JMS Consumer that receives requests about valid epics channels from
 * the instrument code. Returns the list of the valid (authorized) epics
 * channels that can be monitored through the GMP.
 */
public class EpicsConfigRequestConsumer implements MessageListener, ExceptionListener {

    private static final Logger LOG = Logger.getLogger(EpicsConfigRequestConsumer.class.getName());

    private Connection _connection;
    private Session _session;
    private MessageConsumer _consumer;

    private EpicsConfiguration _epicsConfiguration;

    public EpicsConfigRequestConsumer(JmsProvider provider, EpicsConfiguration config) {

        ConnectionFactory factory = provider.getConnectionFactory();
        _epicsConfiguration = config;
         try {
            _connection = factory.createConnection();
            _connection.setClientID("Epics Configuration Request Consumer");
            _connection.start();
            _connection.setExceptionListener(this);
            _session = _connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            //Requests come from a queue
            Destination destination = _session.createQueue(
                    JmsKeys.GMP_GEMINI_EPICS_REQUEST_DESTINATION);
            _consumer = _session.createConsumer(destination);
            _consumer.setMessageListener(this);

            LOG.info(
                    "Message Consumer started to receive epics config requests");
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception starting up Service Request Consumer", e);
        }

    }

    public void onMessage(Message message) {

        try {
            //let's see if it contains a valid request
            
            boolean isEpicsRequest = message.getBooleanProperty(JmsKeys.GMP_GEMINI_EPICS_CHANNEL_PROPERTY);

            if (!isEpicsRequest) return;

            //get the information to return the answer.
            Destination destination = message.getJMSReplyTo();
            if (destination == null) {
                LOG.warning("Invalid destination received. Can't reply to request");
                return;
            }

            MessageProducer replyProducer = _session.createProducer(destination);

            MapMessage replyMessage = _session.createMapMessage();

            for(String name: _epicsConfiguration.getValidChannelsNames()) {
                replyMessage.setBoolean(name, true);
            }

            replyProducer.send(replyMessage);
        } catch (InvalidDestinationException ex) {
            LOG.log(Level.WARNING, "Destination has been destroyed", ex);
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception parsing Epics Request", e);
        }


    }

    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on Epics Config Request Consumer", e);
    }

    public void close() {
        try {
            if (_consumer != null)
                _consumer.close();
            if (_session != null)
                _session.close();
            if (_connection != null)
                _connection.close();
            
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception closing Epics Config Request Consumer: ", e);
        }
    }
}
