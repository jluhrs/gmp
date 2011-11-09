package edu.gemini.cas;

import gov.aps.jca.CAException;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;

/**
 * Interface AlarmChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 1/17/11
 */
public interface AlarmChannel<T> extends ServerChannel<T> {
    /**
     * Resets the alarm status and severity to NO_ALARM
     *
     * @throws gov.aps.jca.CAException if called on a Channel that doesn't support alarms
     */
    void clearAlarm() throws CAException;

    /**
     * Changes the alarm state on the given channel.
     *
     * @param status   the cause of the alarm
     * @param severity whether or not the alarm is catastrophic
     * @param message  used to describe the cause of the alarm
     * @throws CAException
     */
    void setAlarm(Status status, Severity severity, String message) throws CAException;

}
