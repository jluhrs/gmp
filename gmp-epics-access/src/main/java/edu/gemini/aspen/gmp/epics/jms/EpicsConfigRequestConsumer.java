package edu.gemini.aspen.gmp.epics.jms;

import edu.gemini.jms.api.*;
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
public class EpicsConfigRequestConsumer implements MessageListener {

    private static final Logger LOG = Logger.getLogger(EpicsConfigRequestConsumer.class.getName());


    private final BaseMessageConsumer _messageConsumer;
    private final ReplyMessageSender _replySender;

    private final EpicsConfiguration _epicsConfiguration;

    public EpicsConfigRequestConsumer(JmsProvider provider, EpicsConfiguration config) {

        _epicsConfiguration = config;
        _messageConsumer = new BaseMessageConsumer("Epics Configuration Request Consumer",
                new DestinationData(JmsKeys.GMP_GEMINI_EPICS_REQUEST_DESTINATION, DestinationType.QUEUE),
                this);
        _replySender = new ReplyMessageSender("Epics Configuration Request Reply");
        try {
            _messageConsumer.startJms(provider);
            _replySender.startJms(provider);
            LOG.info(
                    "Message Consumer started to receive epics config requests");
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception starting up Service Request Consumer", e);
        }

    }

    private static class ReplyMessageSender extends BaseMessageProducer {

        public ReplyMessageSender(String clientName) {
            super(clientName, null);
        }

        public void send(Destination d, Iterable<String> validChannelsNames) throws JMSException {
            MapMessage replyMessage = _session.createMapMessage();

            for (String name : validChannelsNames) {
                replyMessage.setBoolean(name, true);
            }
            _producer.send(d, replyMessage);
        }
    }

    @Override
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

            _replySender.send(destination, _epicsConfiguration.getValidChannelsNames());
        } catch (InvalidDestinationException ex) {
            LOG.log(Level.WARNING, "Destination has been destroyed", ex);
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception parsing Epics Request", e);
        }


    }

    public void close() {
        _messageConsumer.stopJms();
        _replySender.stopJms();
    }
}
