package edu.gemini.aspen.gmp.data;

/**
 * A handler that will be invoked whenever a new observation event
 * arrives. 
 */

public interface ObservationEventHandler {

    void onObservationEvent(ObservationEvent event, Dataset dataset);

}
