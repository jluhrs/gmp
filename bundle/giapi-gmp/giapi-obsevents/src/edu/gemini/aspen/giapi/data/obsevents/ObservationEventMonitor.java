package edu.gemini.aspen.giapi.data.obsevents;

import edu.gemini.aspen.gmp.data.ObservationEventHandler;

/**
 * An Observation event monitor provides mechanisms to register/unregister
 * Observation event handlers. When the Observation Event Monitor starts, it
 * will start receiving Observation Events (through an underlying communication
 * mechanism, like JMS) and will notify the registered handlers of
 * such events. 
 */
public interface ObservationEventMonitor {

    void registerHandler(ObservationEventHandler handler);

    void unregisterHandler(ObservationEventHandler handler);

}
