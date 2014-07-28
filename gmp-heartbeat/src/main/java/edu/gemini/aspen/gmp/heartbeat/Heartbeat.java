package edu.gemini.aspen.gmp.heartbeat;

import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.setter.StatusSetter;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.gmp.top.Top;
import edu.gemini.jms.api.*;

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
public class Heartbeat implements JmsArtifact {
    private static final Logger LOG = Logger.getLogger(Heartbeat.class.getName());
    private final StatusSetter heartbeatSetter;
    private final Top top;
    private final String heartbeatName;
    private final boolean sendJms;

    private class HeartbeatMessageProducer extends BaseMessageProducer implements Runnable {
        public HeartbeatMessageProducer() {
            super(heartbeatName, new DestinationData(JmsKeys.GMP_HEARTBEAT_DESTINATION, DestinationType.TOPIC));
        }

        private int counter = 0;

        @Override
        public synchronized void run() {
            try {
                if (++counter >= Integer.MAX_VALUE) counter = 0;
                if (sendJms) {
                    BytesMessage m = _session.createBytesMessage();
                    m.writeInt(counter);
                    _producer.send(m);
                }
                heartbeatSetter.setStatusItem(new BasicStatus<Integer>(top.buildStatusItemName(heartbeatName), counter));
            } catch (JMSException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private final HeartbeatMessageProducer producer;
    private ScheduledThreadPoolExecutor executor;
    private ScheduledFuture future;

    public Heartbeat(String heartbeatName,
                     boolean sendJms,
                     Top top,
                     StatusSetter heartbeatSetter) {
        LOG.info("Heartbeat Constructor");
        this.top = top;
        this.heartbeatName = heartbeatName;
        this.sendJms = sendJms;
        producer = new HeartbeatMessageProducer();
        this.heartbeatSetter = heartbeatSetter;
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        LOG.info("Start GMP Heartbeat");
        if (sendJms) {
            try {
                producer.startJms(provider);
            } catch (JMSException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        executor = new ScheduledThreadPoolExecutor(1);
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        future = executor.scheduleAtFixedRate(producer, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stopJms() {
        LOG.info("Stop GMP Heartbeat");
        future.cancel(false);
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        if (sendJms) {
            producer.stopJms();
        }
    }

    public void stopService() {
        stopJms();
    }
}
