package edu.gemini.aspen.gmp.heartbeat.jms;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;

import javax.jms.JMSException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class HeartbeatConsumer
 *
 * @author Nicolas A. Barriga
 *         Date: 12/29/10
 */
public class HeartbeatConsumer{
    public static final Logger LOG = Logger.getLogger(HeartbeatConsumer.class.getName());
    private BaseMessageConsumer consumer;
    private HeartbeatListener hbl;
    public HeartbeatConsumer() {
        hbl = new HeartbeatListener();
        consumer = new BaseMessageConsumer("HeartbeatConsumer", new DestinationData(JmsKeys.GMP_HEARTBEAT_DESTINATION, DestinationType.TOPIC), hbl);

    }

    public void start(String url) {
        try {
            consumer.startJms(new ActiveMQJmsProvider(url));
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void stop(){
        consumer.stopJms();
    }

    public long getLast(){
        return hbl.getLast();
    }
}
