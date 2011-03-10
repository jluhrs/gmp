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

    private class HeartbeatListener implements MessageListener {
        private final Logger LOG = Logger.getLogger(HeartbeatListener.class.getName());

        @Override
        public void onMessage(Message message) {
            if (message instanceof BytesMessage) {
                BytesMessage bm = (BytesMessage) message;
                try {
                    notifyConsumers(bm.readLong());
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

    private void notifyConsumers(long beatNumber){
        for(IHeartbeatConsumer consumer:consumers){
            consumer.beat(beatNumber);
        }
    }

    private HeartbeatDistributor(){
        LOG.info("HeartbeatDistributor Constructor");
        hbConsumer = new JmsHeartbeatConsumer("HeartbeatDistributor",new HeartbeatListener());
    }

    public HeartbeatDistributor(JmsProvider provider){
        LOG.info("HeartbeatDistributor Constructor");
        this.provider=provider;
        hbConsumer = new JmsHeartbeatConsumer("HeartbeatDistributor",new HeartbeatListener());
    }

    @Validate
    public void start(){
        LOG.info("HeartbeatDistributor Validate");
        hbConsumer.start(provider);
    }

    @Invalidate
    public void stop(){
        LOG.info("HeartbeatDistributor InValidate");
        hbConsumer.stop();
    }

    @Bind(aggregate = true)
    public void bindHeartbeatConsumer(IHeartbeatConsumer consumer) {
        consumers.add(consumer);
        LOG.info("Heartbeat Consumer registered at Distributor: " + consumer);
    }

    @Unbind
    public void unbindHeartbeatConsumer(IHeartbeatConsumer consumer) {
        consumers.remove(consumer);
        LOG.info("Removed Heartbeat Consumer from Distributor: " + consumer);
    }

}
