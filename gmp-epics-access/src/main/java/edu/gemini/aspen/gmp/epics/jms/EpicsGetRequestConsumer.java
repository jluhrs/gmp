package edu.gemini.aspen.gmp.epics.jms;

import com.cosylab.epics.caj.CAJChannel;
import com.cosylab.epics.caj.CAJContext;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import edu.gemini.epics.EpicsReader;
import edu.gemini.epics.ReadOnlyClientEpicsChannel;
import edu.gemini.epics.api.DbrUtil;
import edu.gemini.jms.api.*;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;

import javax.jms.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A JMS Consumer that receives requests about valid epics channels from
 * the instrument code. Returns the list of the valid (authorized) epics
 * channels that can be monitored through the GMP.
 */
public class EpicsGetRequestConsumer implements MessageListener {

    private static final Logger LOG = Logger.getLogger(EpicsGetRequestConsumer.class.getName());


    private final BaseMessageConsumer _messageConsumer;
    private final ChannelGetReplyMessageSender _replySender;
    private final EpicsReader epicsReader;

    public EpicsGetRequestConsumer(JmsProvider provider, EpicsReader epicsReader) {
        this.epicsReader = epicsReader;

        _messageConsumer = new BaseMessageConsumer("Epics Get Request Consumer",
                new DestinationData(JmsKeys.GMP_GEMINI_EPICS_GET_DESTINATION, DestinationType.TOPIC),
                this);
        _replySender = new ChannelGetReplyMessageSender("Epics Get Request Reply");
        try {
            _messageConsumer.startJms(provider);
            _replySender.startJms(provider);
            LOG.info(
                    "Message Consumer started to receive epics get requests");
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception starting up Service Request Consumer", e);
        }

    }

    private static class ChannelGetReplyMessageSender extends BaseMessageProducer {

        public ChannelGetReplyMessageSender(String clientName) {
            super(clientName, null);
        }

        public <T> EpicsUpdateImpl<T> valueChanged(String channel, List<T> values) {
            return new EpicsUpdateImpl<T>(channel, values);
        }

        public void send(Destination d, String channelName, DBR dbr) throws JMSException {
            List<?> values = DbrUtil.extractValues(dbr);

            Message replyMessage = EpicsJmsFactory.createMessage(_session, valueChanged(channelName, values));

            _producer.send(d, replyMessage);
        }
    }

    @Override
    public void onMessage(Message message) {

        try {
            //let's see if it contains a valid request
            final String channelName = message.getStringProperty(JmsKeys.GMP_GEMINI_EPICS_CHANNEL_PROPERTY);

            if (channelName == null || channelName.isEmpty()) return;

            //get the information to return the answer.
            final Destination destination = message.getJMSReplyTo();
            if (destination != null) {
                try {
                    ReadOnlyClientEpicsChannel<?> channel = epicsReader.getChannelAsync(channelName);
                    if (channel != null) {
                        _replySender.send(destination, channelName, channel.getDBR());
                    } else {
                        LOG.log(Level.SEVERE, "Cannot open channel " + channelName);
                    }
                } catch (CAException e) {
                    LOG.log(Level.SEVERE, "Exception reading channelName " + channelName, e);
                } catch (TimeoutException e) {
                    LOG.log(Level.SEVERE, "Timed out waiting for channelName " + channelName, e);
                }
            } else {
                LOG.warning("Invalid destination received. Can't reply to request");
            }

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
