package edu.gemini.aspen.giapi.status;

/**
 * Interface to describe Alarm Status Items
 */
public interface AlarmStatusItem<T> extends StatusItem<T> {

    /**
     * Get the Alarm State for this status item
     * @return the Alarm state for this status item. The
     * Alarm State can not be null.
     */
    AlarmState getAlarmState();
    
}
