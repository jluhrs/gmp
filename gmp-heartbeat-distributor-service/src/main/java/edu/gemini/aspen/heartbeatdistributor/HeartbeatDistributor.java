package edu.gemini.aspen.heartbeatdistributor;

import edu.gemini.aspen.gmp.heartbeat.jms.JmsHeartbeatConsumer;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class HeartbeatDistributor
 *
 * @author Nicolas A. Barriga
 *         Date: 3/10/11
 */
public class HeartbeatDistributor implements JmsArtifact {
    private static final Logger LOG = Logger.getLogger(HeartbeatDistributor.class.getName());
    private final List<HeartbeatConsumer> consumers = new CopyOnWriteArrayList<HeartbeatConsumer>();

    /**
     * Implements the JMS message listener
     */
    private class HeartbeatListener implements MessageListener {
        private final Logger LOG = Logger.getLogger(HeartbeatListener.class.getName());

        @Override
        public void onMessage(Message message) {
            if (message instanceof BytesMessage) {
                BytesMessage bm = (BytesMessage) message;
                try {
                    notifyConsumers(bm.readInt());
                } catch (JMSException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            } else {
                LOG.warning("Wrong message type");
            }
        }
    }

    private JmsHeartbeatConsumer hbConsumer;

    /**
     * Notify all registered consumers. Sends the beat number.
     *
     * @param beatNumber beat number to notify.
     */
    private void notifyConsumers(int beatNumber) {
        for (HeartbeatConsumer consumer : consumers) {
            try {
                consumer.beat(beatNumber);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Exception updating a HeartbeatConsumer", ex);
            }
        }
    }

    /**
     * Public constructor. To manually create a distributor.
     */
    public HeartbeatDistributor() {
        LOG.fine("HeartbeatDistributor Constructor");
        hbConsumer = new JmsHeartbeatConsumer("HeartbeatDistributor", new HeartbeatListener());
    }


    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        hbConsumer.start(provider);
        LOG.info("HeartbeatDistributor started.");

    }

    @Override
    public void stopJms() {
        hbConsumer.stop();
        LOG.info("HeartbeatDistributor stopped.");
    }


    /**
     * Registers Heartbeat consumers. Automatically called by iPOJO when components implementing HeartbeatConsumer
     * are activated.
     *
     * @param consumer the consumer to register
     */
    public void bindHeartbeatConsumer(HeartbeatConsumer consumer) {
        consumers.add(consumer);
        LOG.info("Heartbeat Consumer registered at Distributor: " + consumer);
    }

    /**
     * Unregisters Heartbeat consumers. Automatically called by iPOJO when components implementing HeartbeatConsumer
     * are deactivated.
     *
     * @param consumer the consumer to unregister
     */
    public void unbindHeartbeatConsumer(HeartbeatConsumer consumer) {
        consumers.remove(consumer);
        LOG.info("Removed Heartbeat Consumer from Distributor: " + consumer);
    }

}
