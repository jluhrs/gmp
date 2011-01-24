package edu.gemini.aspen.giapi.data;

import java.util.Map;
import java.util.HashMap;

/**
 * Definiton of the GIAPI Observation Events. 
 *
 */
public enum ObservationEvent {
    /**
     * Event sent as instrument starts preparation for starting
     * acquisition of a dataset.
     */
    OBS_PREP("OBS_PREP"),
    /**
     * Event sent just before data acquisition starts.
     */
    OBS_START_ACQ("OBS_START_ACQ"),
    /**
     * Event sent when the requested acquisition has completed.
     */
    OBS_END_ACQ("OBS_END_ACQ"),
    /**
     * Event indicates that data is being transferred from the
     * detector or other activities needed to write data.
     */
    OBS_START_READOUT("OBS_START_READOUT"),
    /**
     * Event indicates readout or write preparations have completed
     */
    OBS_END_READOUT("OBS_END_READOUT"),
    /**
     * Event indicates that the instrument has started writing
     * the dataset to GSDN
     */
    OBS_START_DSET_WRITE("OBS_START_DSET_WRITE"),
    /**
     * Event indicates that the instrument has completed writing
     * the dataset to GSDN
     */
    OBS_END_DSET_WRITE("OBS_END_DSET_WRITE");


    private String eventName;

    private static Map<String, ObservationEvent> CONVERSION_TABLE = new HashMap<String, ObservationEvent>();

    /**
     * Map observation event names to the corresponding enum values. 
     */
    static {
        for (ObservationEvent event: ObservationEvent.values()) {
            CONVERSION_TABLE.put(event.getObservationEventName(), event);
        }
    }

    private ObservationEvent(String name) {
        eventName = name;
    }

    public String getObservationEventName() {
        return eventName;
    }

    public static ObservationEvent getObservationEvent(String name) throws IllegalArgumentException {
        ObservationEvent event = CONVERSION_TABLE.get(name);
        if (event == null) {
            throw new IllegalArgumentException("No such observation event: " + name);
        }
        return event;
    }


}
