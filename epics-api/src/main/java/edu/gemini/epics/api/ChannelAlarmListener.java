package edu.gemini.epics.api;

import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;

import java.util.List;

/**
 * Interface ChannelAlarmListener
 *
 * @author Nicolas A. Barriga
 *         Date: 6/28/12
 */
public interface ChannelAlarmListener<T> extends EpicsListener<T>{
    /**
     * Called when the specified channel value changes.
     *
     * @param channelName name of the epics channel that changed.
     * @param values      the new values for the epics channel
     */
    void valueChanged(String channelName, List<T> values, Status status, Severity severity);
}
