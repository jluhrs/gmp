package edu.gemini.aspen.gmp.heartbeat;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.BaseMessageProducer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;

import javax.jms.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


/**
 * Class Heartbeat
 *
 * @author Nicolas A. Barriga
 *         Date: 12/29/10
 */
public class Heartbeat extends BaseMessageProducer {
    private ScheduledThreadPoolExecutor executor;
    private ScheduledFuture future;
    class HeartbeatSender implements Runnable {
        private long counter=0;
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

    public Heartbeat() {
        super("Heartbeat", new DestinationData(JmsKeys.GMP_HEARTBEAT_DESTINATION, DestinationType.TOPIC));

    }

    public void start(String url) {
        try {
            startJms(new ActiveMQJmsProvider(url));
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        executor = new ScheduledThreadPoolExecutor(1);
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        future = executor.scheduleAtFixedRate(new HeartbeatSender(), 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        future.cancel(false);
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            //todo:check this exception management
            throw new RuntimeException(ex);
        }
        stopJms();
    }

}
