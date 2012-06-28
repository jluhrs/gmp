package edu.gemini.epics.api;

import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;

import java.util.List;

/**
 * Interface ReadOnlyChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 11/8/11
 */
public interface ReadOnlyChannel<T> {
    /**
     * Retrieves a value from a PV
     *
     * @return a DBR containing the value(s) of the process variable
     * @throws gov.aps.jca.CAException if read couldn't be completed
     * @throws IllegalStateException   if Channel is not correctly initialized
     */
    DBR getDBR() throws CAException, TimeoutException;

    /**
     * Convenience method to retrieve a value from a PV in a List<T>
     *
     * @return a List<T> containing the value(s) of the process variable
     * @throws gov.aps.jca.CAException if read couldn't be completed
     * @throws IllegalStateException   if Channel is not correctly initialized
     */
    List<T> getAll() throws CAException, TimeoutException;

    /**
     * Convenience method to retrieve a value from a PV. This method is provided because all of our PVs have only
     * one value.
     *
     * @return a T containing the first value of the process variable
     * @throws gov.aps.jca.CAException if read couldn't be completed
     * @throws IllegalStateException   if Channel is not correctly initialized
     */
    T getFirst() throws CAException, TimeoutException;

    /**
     * Returns the Channel name
     *
     * @return the Channel name
     */
    String getName();

    /**
     * Register a listener to be notified every time the channel value changes
     *
     * @param listener
     */
    void registerListener(ChannelListener<T> listener) throws CAException;

    void unRegisterListener(ChannelListener<T> listener) throws CAException;

    void registerListener(ChannelAlarmListener<T> listener) throws CAException;

    void unRegisterListener(ChannelAlarmListener<T> listener) throws CAException;

    /**
     * Indicates whether the channel is valid/connected
     */
    boolean isValid();

    /**
     * Returns the type of the underlying EPICS channel.
     *
     * @return
     */
    DBRType getType();
}
