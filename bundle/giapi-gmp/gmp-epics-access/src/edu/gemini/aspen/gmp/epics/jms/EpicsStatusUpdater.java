package edu.gemini.aspen.gmp.epics.jms;

import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.epics.EpicsUpdateListener;
import edu.gemini.aspen.gmp.epics.EpicsConfiguration;
import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.TreeMap;

/**
 * This EPICS Update listener will receive Epics Updates and will dispatch
 * them via JMS asynchronously. Basically, every update will be put in a
 * dispatch queue, where an internal thread will be taking the updates and
 * dispatching them via JMS.
 */
public class EpicsStatusUpdater implements ExceptionListener, EpicsUpdateListener {

    private static final Logger LOG = Logger.getLogger(EpicsStatusUpdater.class.getName());

    private Connection _connection;
    private Session _session;

    private MessageProducer _producer;


    private Map<String, Destination> _destinationMap = new TreeMap<String, Destination>();


    public EpicsStatusUpdater(JmsProvider provider, EpicsConfiguration config) {

        ConnectionFactory connectionFactory = provider.getConnectionFactory();
        try {
            _connection = connectionFactory.createConnection();
            _connection.setClientID("Epics Status Updater");
            _connection.start();
            _connection.setExceptionListener(this);
            _session = _connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            _producer = _session.createProducer(null);
            //Create destinations for all the channels to be broadcasted to the instrument

            for (String channelName : config.getValidChannelsNames()) {
                String topic = GmpKeys.GMP_GEMINI_EPICS_TOPIC_PREFIX + channelName.toUpperCase();
                _destinationMap.put(channelName, _session.createTopic(topic));
            }

        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
        LOG.info("Epics Status Updater started");
    }


    public void close() {
        try {
            if (_producer != null)
                _producer.close();
            if (_session != null)
                _session.close();
            if (_connection != null)
                _connection.close();
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception closing Epics Status Updater : ", e);
        }
    }


    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on Epics Status Updater", e);
    }

    public void onEpicsUpdate(EpicsUpdate update) {

        try {
            //send the update via JMS
            Destination d = _destinationMap.get(update.getChannelName());

            if (d != null) {
                Message m = EpicsJmsFactory.createMessage(_session, update);
                if (m != null) {
                    LOG.fine("Updating channel: " + update.getChannelName() + " to " + d);
                    _producer.send(d, m);
                }
            }
        }
        catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem sending Epics Status Update via JMS: ", e);
        }

    }

}
