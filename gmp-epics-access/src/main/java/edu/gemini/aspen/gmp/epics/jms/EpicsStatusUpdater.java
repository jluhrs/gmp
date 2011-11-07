package edu.gemini.aspen.gmp.epics.jms;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.BaseMessageProducer;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.epics.EpicsUpdateListener;
import edu.gemini.aspen.gmp.epics.EpicsConfiguration;
import edu.gemini.aspen.gmp.epics.EpicsUpdate;

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
public class EpicsStatusUpdater implements EpicsUpdateListener {

    private static final Logger LOG = Logger.getLogger(EpicsStatusUpdater.class.getName());

    private final UnidentifiedMessageSender _sender;

    private Map<String, String> _topicMap = new TreeMap<String, String>();

    private static class UnidentifiedMessageSender extends BaseMessageProducer {

        public UnidentifiedMessageSender(String clientName) {
            super(clientName, null);
        }

        public void send(String topic, EpicsUpdate<?> update) throws JMSException {
            _producer.send(_session.createTopic(topic),
                    EpicsJmsFactory.createMessage(_session, update));
        }
    }

    public EpicsStatusUpdater(JmsProvider provider, EpicsConfiguration config) {
        _sender = new UnidentifiedMessageSender("Epics Status Updater");

        try {
            _sender.startJms(provider);
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
        //Create destinations for all the channels to be broadcasted to the instrument
        for (String channelName : config.getValidChannelsNames()) {
            String topic = JmsKeys.GMP_GEMINI_EPICS_TOPIC_PREFIX + channelName.toUpperCase();
            _topicMap.put(channelName, topic);
        }
        LOG.info("Epics Status Updater started");
    }


    public void close() {
        _sender.stopJms();

    }


    @Override
    public void onEpicsUpdate(EpicsUpdate<?> update) {

        try {
            //send the update via JMS
            if (_topicMap.containsKey(update.getChannelName())) {
                String topic = _topicMap.get(update.getChannelName());
                LOG.fine("Updating channel: " + update.getChannelName() + " to " + topic);
                _sender.send(topic, update);
            }
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem sending Epics Status Update via JMS: ", e);
        }

    }

}
