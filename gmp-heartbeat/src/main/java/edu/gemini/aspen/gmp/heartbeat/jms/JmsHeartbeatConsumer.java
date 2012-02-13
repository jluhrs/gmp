package edu.gemini.aspen.gmp.heartbeat.jms;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class JmsHeartbeatConsumer
 *
 * @author Nicolas A. Barriga
 *         Date: 12/29/10
 */
public class JmsHeartbeatConsumer {
    private static final Logger LOG = Logger.getLogger(JmsHeartbeatConsumer.class.getName());
    private BaseMessageConsumer consumer;
    private MessageListener hbl;

    public JmsHeartbeatConsumer(String name, MessageListener listener) {
        hbl = listener;
        consumer = new BaseMessageConsumer(name, new DestinationData(JmsKeys.GMP_HEARTBEAT_DESTINATION, DestinationType.TOPIC), hbl);

    }

    public void start(JmsProvider provider) {
        try {
            consumer.startJms(provider);
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void stop() {
        consumer.stopJms();
    }

}
