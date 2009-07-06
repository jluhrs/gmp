package edu.gemini.aspen.giapi.data.obsevents;

import edu.gemini.aspen.gmp.data.ObservationEventHandler;

import java.util.Set;

/**
 * This interface defines a composite for {@link ObservationEventHandler}s.
 * When an Observation Event occurs, the method
 * {@link #onObservationEvent(edu.gemini.aspen.gmp.data.ObservationEvent, edu.gemini.aspen.gmp.data.Dataset)}
 * will invoke all the registerd handlers, so they get and process the
 * received event.
 */
public interface ObservationEventHandlerComposite extends ObservationEventHandler {

    /**
     * Register a new handler in the composite
     * @param handler new handler to be registered. Only one copy of any
     * given handler is allowed
     */
    void registerHandler(ObservationEventHandler handler);

    /**
     * Remove the handler from this composite
     * @param handler the handler to remove.
     */
    void unregisterHandler(ObservationEventHandler handler);

    /**
     * Return an unmodifiable view of the handlers registered
     * in this composite.
     * @return an unmodifiable {@link Set} containing the handlers
     * registered in this composite
     */
    Set<ObservationEventHandler> getHandlers();
}
