package edu.gemini.aspen.heartbeatdistributor;

import edu.gemini.aspen.gmp.heartbeat.jms.JmsHeartbeatConsumer;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class HeartbeatDistributor
 *
 * @author Nicolas A. Barriga
 *         Date: 3/10/11
 */
@Component
@Instantiate
public class HeartbeatDistributor {
    private static final Logger LOG = Logger.getLogger(HeartbeatDistributor.class.getName());
    private final List<IHeartbeatConsumer> consumers = Collections.synchronizedList(new ArrayList<IHeartbeatConsumer>());

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

    @Requires
    private JmsProvider provider;

    /**
     * Notify all registered consumers. Sends the beat number.
     *
     * @param beatNumber beat number to notify.
     */
    private void notifyConsumers(int beatNumber){
        for(IHeartbeatConsumer consumer:consumers){
            consumer.beat(beatNumber);
        }
    }

    /**
     * Private constructor. Used by iPOJO.
     */
    private HeartbeatDistributor(){
        LOG.info("HeartbeatDistributor Constructor");
        hbConsumer = new JmsHeartbeatConsumer("HeartbeatDistributor",new HeartbeatListener());
    }

    /**
     * Public constructor. To manually create a distributor.
     *
     * @param provider the JmsProvider to get JMS messages from.
     */
    public HeartbeatDistributor(JmsProvider provider){
        LOG.info("HeartbeatDistributor Constructor");
        this.provider=provider;
        hbConsumer = new JmsHeartbeatConsumer("HeartbeatDistributor",new HeartbeatListener());
    }

    /**
     * Starts listening for JMS messages
     */
    @Validate
    public void start(){
        LOG.info("HeartbeatDistributor Validate");
        hbConsumer.start(provider);
    }

    /**
     * Stops listening for JMS messages
     */
    @Invalidate
    public void stop(){
        LOG.info("HeartbeatDistributor InValidate");
        hbConsumer.stop();
    }

    /**
     * Registers Heartbeat consumers. Automatically called by iPOJO when components implementing IHeartbeatConsumer
     * are activated.
     *
     * @param consumer the consumer to register
     */
    @Bind(aggregate = true)
    public void bindHeartbeatConsumer(IHeartbeatConsumer consumer) {
        consumers.add(consumer);
        LOG.info("Heartbeat Consumer registered at Distributor: " + consumer);
    }

    /**
     * Unregisters Heartbeat consumers. Automatically called by iPOJO when components implementing IHeartbeatConsumer
     * are deactivated.
     *
     * @param consumer the consumer to unregister
     */
    @Unbind
    public void unbindHeartbeatConsumer(IHeartbeatConsumer consumer) {
        consumers.remove(consumer);
        LOG.info("Removed Heartbeat Consumer from Distributor: " + consumer);
    }

}
