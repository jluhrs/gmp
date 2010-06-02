package edu.gemini.aspen.giapi.data;

/**
 * A handler that will be invoked whenever a new observation event
 * arrives. 
 */

public interface ObservationEventHandler {

    /**
     * Invoked when a new Observation Event arrives. The framework
     * invokes this method in a separate thread.
     * @param event  Observation Event received
     * @param dataset dataset associated to this observation event
     */
    void onObservationEvent(ObservationEvent event, Dataset dataset);

}
