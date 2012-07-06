package edu.gemini.aspen.gmp.heartbeat;

import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.status.IStatusSetter;
import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.jms.api.*;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class Heartbeat sends an integer increasing every 1 second, and wrapping at 1000, back to 0.
 *
 * @author Nicolas A. Barriga
 *         Date: 12/29/10
 */
@Component
@Instantiate
@Provides
public class Heartbeat implements JmsArtifact {
    private static final Logger LOG = Logger.getLogger(Heartbeat.class.getName());
    private final String heartbeatName = "gmp:heartbeat";
    private final IStatusSetter heartbeatSetter;
    private final Top top;

    private class HeartbeatMessageProducer extends BaseMessageProducer implements Runnable {
        public HeartbeatMessageProducer() {
            super("Heartbeat", new DestinationData(JmsKeys.GMP_HEARTBEAT_DESTINATION, DestinationType.TOPIC));
        }

        private int counter = 0;

        @Override
        public synchronized void run() {
            try {
                BytesMessage m = _session.createBytesMessage();
                m.writeInt(counter++);
                if (counter >= Integer.MAX_VALUE) counter = 0;
                _producer.send(m);
                heartbeatSetter.setStatusItem(new BasicStatus<Integer>(top.buildStatusItemName(heartbeatName), counter));
            } catch (JMSException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private final HeartbeatMessageProducer producer;
    private ScheduledThreadPoolExecutor executor;
    private ScheduledFuture future;

    public Heartbeat(@Requires Top top, @Requires IStatusSetter heartbeatSetter) {
        LOG.info("Heartbeat Constructor");
        this.top = top;
        producer = new HeartbeatMessageProducer();
        this.heartbeatSetter = heartbeatSetter;
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        try {
            producer.startJms(provider);
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        executor = new ScheduledThreadPoolExecutor(1);
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        future = executor.scheduleAtFixedRate(producer, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stopJms() {
        LOG.info("Heartbeat InValidate");
        future.cancel(false);
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        producer.stopJms();
    }
}
