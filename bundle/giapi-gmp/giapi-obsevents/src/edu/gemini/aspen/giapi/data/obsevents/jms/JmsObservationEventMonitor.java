package edu.gemini.aspen.giapi.data.obsevents.jms;

import edu.gemini.aspen.gmp.util.jms.GmpKeys;
import edu.gemini.aspen.gmp.data.ObservationEvent;
import edu.gemini.aspen.gmp.data.Dataset;
import edu.gemini.aspen.gmp.data.ObservationEventHandler;
import edu.gemini.jms.api.AbstractMessageConsumer;
import edu.gemini.aspen.giapi.data.obsevents.ObservationEventMonitor;

import javax.jms.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;

/**
 * This class is an implementation of the ObservationEventMonitor interface that will receive
 * observation events via JMS messages, and will process them with the registered handlers.
 */
public class JmsObservationEventMonitor extends AbstractMessageConsumer implements ObservationEventMonitor {

    private static final Logger LOG = Logger.getLogger(JmsObservationEventMonitor.class.getName());

    /**
     * Each observation handler will be invoked in its own thread. Each
     * handler must use the same thread all the time to make sure events
     * arrive in the same order as they are received. We'll mantain
     * a map with the executor service to be used by each one of the
     * registered handlers.
     */
    private Map<ObservationEventHandler, ExecutorService> _handlerMap =
            new ConcurrentHashMap<ObservationEventHandler, ExecutorService>();


    public JmsObservationEventMonitor() {
        super("JMS Observation Event Monitor", GmpKeys.GMP_DATA_OBSEVENT_DESTINATION);
    }

    public void onMessage(Message m) {

        if (m == null) LOG.warning("A null message was received through the observation event channel");

        try {
            String type = m.getStringProperty(GmpKeys.GMP_DATA_OBSEVENT_NAME);
            String file = m.getStringProperty(GmpKeys.GMP_DATA_OBSEVENT_FILENAME);
            ObservationEvent obsEvent = ObservationEvent.getObservationEvent(type);
            Dataset dataset = new Dataset(file);
            processEvent(obsEvent, dataset);
        } catch (JMSException e) {
            LOG.warning("Jms Exception: " + e.getMessage());
        } catch (IllegalArgumentException ex) {
            //an unexpected arg came in the meesages
            LOG.warning("Bad argument in message: " + ex.getMessage());
        }

    }


    private void processEvent(final ObservationEvent event, final Dataset dset) {

        for (final ObservationEventHandler handler : _handlerMap.keySet()) {
            try {
                _handlerMap.get(handler).execute(new Runnable() {
                    public void run() {
                        handler.onObservationEvent(event, dset);
                    }
                });
            } catch (RejectedExecutionException ex) {
                if (!_handlerMap.get(handler).isShutdown()) {
                    LOG.log(Level.SEVERE, "Task execution rejected by executor service ", ex);
                }
            }
        }
    }


    public void registerHandler(ObservationEventHandler handler) {
        if (handler == null) {
            LOG.warning("You can't register a null Observation Event Handler");
            return;
        }
        _handlerMap.put(handler, Executors.newSingleThreadExecutor());
    }

    public void unregisterHandler(ObservationEventHandler handler) {
        
        _handlerMap.remove(handler);
    }


}
