package edu.gemini.aspen.gmp.heartbeat;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.BaseMessageProducer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class Heartbeat
 *
 * @author Nicolas A. Barriga
 *         Date: 12/29/10
 */
@Component
@Instantiate
public class Heartbeat{
    protected static final Logger LOG = Logger.getLogger(Heartbeat.class.getName());

    private class HeartbeatMessageProducer extends BaseMessageProducer implements Runnable{
        public HeartbeatMessageProducer(){
             super("Heartbeat", new DestinationData(JmsKeys.GMP_HEARTBEAT_DESTINATION, DestinationType.TOPIC));
        }
        private long counter = 0;

            @Override
            public synchronized void run() {
                try {
                    BytesMessage m = _session.createBytesMessage();
                    m.writeLong(counter++);
                    _producer.send(m);
                } catch (JMSException e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                }
            }
    }
    private HeartbeatMessageProducer producer;

    private ScheduledThreadPoolExecutor executor;
    private ScheduledFuture future;

    @Requires
    private JmsProvider provider;

    private Heartbeat() {
        producer=new HeartbeatMessageProducer();
    }

    public Heartbeat(JmsProvider provider) {
        producer=new HeartbeatMessageProducer();
        this.provider=provider;
    }

    @Validate
    public void start() {
        try {
            producer.startJms(provider);
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        executor = new ScheduledThreadPoolExecutor(1);
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        future = executor.scheduleAtFixedRate(producer, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Invalidate
    public void stop() {
        future.cancel(false);
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            //todo:check this exception management
            throw new RuntimeException(ex);
        }
        producer.stopJms();
    }

}
