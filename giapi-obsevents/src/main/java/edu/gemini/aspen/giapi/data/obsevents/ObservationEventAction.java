package edu.gemini.aspen.giapi.data.obsevents;

import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEvent;
import edu.gemini.aspen.giapi.data.ObservationEventHandler;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

/**
 * This is the action that is going to be executed whenever a new Observation
 * Event appears in the system. This action is implemented as an
 * {@link edu.gemini.aspen.giapi.data.obsevents.ObservationEventHandlerComposite}
 * allowing the registration of multiple handlers to be invoked whenever
 * a new {@link edu.gemini.aspen.giapi.data.ObservationEvent} is received.
 * <br>
 * The registered handlers are invoked in their own threads, allowing parallel
 * execution of different handlers. This class also makes sure the events are
 * processed by the handlers in the same order they are received.  
 *
 */
public class ObservationEventAction implements ObservationEventHandlerComposite {

    private static final Logger LOG = Logger.getLogger(ObservationEventAction.class.getName());

    /**
     * Each observation handler will be invoked in its own thread. Each
     * handler must use the same thread all the time to make sure events
     * arrive in the same order as they are received. We'll mantain
     * a map with the executor service to be used by each one of the
     * registered handlers.
     */
    private Map<ObservationEventHandler, ExecutorService> _handlerMap =
            new ConcurrentHashMap<ObservationEventHandler, ExecutorService>();

    public void onObservationEvent(final ObservationEvent event, final DataLabel dset) {

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

    public Set<ObservationEventHandler> getHandlers() {
        return Collections.unmodifiableSet(_handlerMap.keySet());
    }

}
