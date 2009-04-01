package edu.gemini.aspen.gmp.epics.jms;

import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.epics.EpicsUpdateListener;
import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsConfiguration;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;

/**
 *
 */
public class EpicsStatusUpdater implements ExceptionListener, EpicsUpdateListener {

    private static final Logger LOG = Logger.getLogger(EpicsStatusUpdater.class.getName());

    private Connection _connection;
    private Session _session;

    private MessageProducer _producer;


    private Map<String, Destination> _destinationMap = new TreeMap<String, Destination>();

    private final BlockingQueue<EpicsUpdate> _updateQueue =
            new LinkedBlockingQueue<EpicsUpdate>();


    /**
     * The executor service provides a separate thread for the Updater thread
     * to run
     */
    private final ExecutorService _executorService =
            Executors.newSingleThreadExecutor();

    /* The update task is responsible for receiving updates through
    * the update queue and notify the clients waiting in the action quueue
    */
    private final UpdaterTask _updaterTask = new UpdaterTask();


    public EpicsStatusUpdater(JmsProvider provider, EpicsConfiguration config) {

        ConnectionFactory connectionFactory = provider.getConnectionFactory();
        try {
            _connection = connectionFactory.createConnection();
            _connection.setClientID("Epics Status Updater");
            _connection.start();
            _connection.setExceptionListener(this);
            _session = _connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            _producer = _session.createProducer(null);
            //this improves performance by avoiding to store the messages
//            _producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            //Create destinations for all the channels to be broadcasted to the instrument

            for (String channelName : config.getValidChannelsNames()) {
                String topic = GmpKeys.GMP_GEMINI_EPICS_TOPIC_PREFIX + channelName.toUpperCase();
                _destinationMap.put(channelName, _session.createTopic(topic));
            }

        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
        LOG.info("Epics Status Updater started");
    }


    /**
     * Start up a background thread used to send the completion
     * information invoking the <code>CompletionListener</code> handlers
     * registered.
     */
    public void start() {
        //Submit the processor task for execution in a separate thread
        _executorService.submit(_updaterTask);
    }

    /*
     ** Stop the processing thread of this action manager.
    */
    public void stop() {
        _updaterTask.stop();
        _executorService.shutdown();
        try {
            if (!_executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                _executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            _executorService.shutdownNow();
        }
    }


     public void close() {
        try {
            if (_producer != null)
                _producer.close();
            if (_session != null)
                _session.close();
            if (_connection != null)
                _connection.close();
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception closing Epics Status Updater : ", e);
        }
    }


    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on Epics Status Updater", e);
    }

    public void onEpicsUpdate(EpicsUpdate update) {
        //put the update on the queue, and keep waiting for more updates
        try {
            _updateQueue.put(update);
        } catch (InterruptedException e) {
            //nothing to do. The operation was interrupted
        }
    }

    private class UpdaterTask implements Runnable {
        private boolean isRunning;


        public void run() {
            isRunning = true;
            while (isRunning()) {
                try {
                    EpicsUpdate update = _updateQueue.take();
                    //send the update via JMS
                    Destination d = _destinationMap.get(update.getChannelName());
                    
                    if (d != null) {
                        Message m = EpicsJmsFactory.createMessage(_session, update);
                        if (m != null) {
                            LOG.info("Updating channel: " + update.getChannelName() + " to " + d);
                            _producer.send(d, m);
                        }
                    }


                } catch (InterruptedException e) {
                    LOG.info("Updater Task Thread interrupted. Exiting");
                    isRunning = false;
                    return;
                } catch (JMSException e) {
                    LOG.log(Level.WARNING, "Problem sending Epics Status Update via JMS: ", e);
                }
            }
        }

        private synchronized boolean isRunning() {

            return isRunning;

        }

        /**
         * Stop the current processing thread.
         */
        public synchronized void stop() {
            isRunning = false;
        }

    }
}
