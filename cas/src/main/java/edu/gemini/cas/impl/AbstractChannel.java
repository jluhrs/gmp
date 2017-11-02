package edu.gemini.cas.impl;

import com.cosylab.epics.caj.cas.ProcessVariableEventDispatcher;
import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import com.google.common.collect.ImmutableList;
import edu.gemini.cas.ServerChannel;
import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import edu.gemini.epics.api.ChannelAlarmListener;
import edu.gemini.epics.api.ChannelListener;
import edu.gemini.epics.api.EpicsListener;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class AbstractChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
abstract class AbstractChannel<T> implements ServerChannel<T> {
    private AlarmMemoryProcessVariable pv;
    private final Map<EpicsListener<T>, ProcessVariableEventCallback> eventCallbacks = new HashMap<EpicsListener<T>, ProcessVariableEventCallback>();
    boolean registered = false;

    @Override
    public boolean isValid() {
        return registered;
    }

    protected AbstractChannel(AlarmMemoryProcessVariable pv) {
        this.pv = pv;
        ProcessVariableEventDispatcher ed = new ProcessVariableEventDispatcher(pv);
        pv.setEventCallback(ed);
    }

    /**
     * Register this channel's process variable in the given server
     *
     * @param server the server to register the pv
     */
    void register(DefaultServerImpl server) {
        server.registerProcessVaribale(pv.getName(), pv);
        registered = true;
    }

    /**
     * Unregister this channel's process variable from the given server. Destroy the pv.
     *
     * @param server the server to unregister the pv from
     */
    void destroy(DefaultServerImpl server) {
        server.unregisterProcessVaribale(getName());
        pv.destroy();
        pv = null;
        registered = false;
    }

    void setAlarmState(Status status, Severity severity) {
        pv.setStatus(status);
        pv.setSeverity(severity);
    }

    /**
     * Check the correctness of the arguments
     *
     * @param values to check
     * @return true if argument type and size matches the channel's, false otherwise
     */
    protected abstract boolean validateArgument(List<T> values);

    /**
     * Construct a DBR of appropriate type, with the given values.
     *
     * @param values to put in the DBR
     * @return a DBR that implements gov.aps.jca.dbr.STS, with the given values
     */
    protected abstract DBR buildDBR(List<T> values);

    /**
     * Construct an empty DBR of appropriate type.
     *
     * @return a DBR that implements gov.aps.jca.dbr.TIME
     */
    protected abstract DBR emptyDBR();

    /**
     * Get the values from the DBR.
     *
     * @param dbr to read values from
     * @return a List of the appropriate type, with the values in the DBR
     */
    protected abstract List<T> extractValues(DBR dbr);

    /**
     * Get the size of the PV's data.
     *
     * @return the size of the PV's data.
     */
    protected int getSize() {
        return pv.getDimensionSize(0);
    }

    /**
     * Checks if this channel represents a Double
     *
     * @return true if Double, false otherwise
     */
    public boolean isDouble() {
        return pv.getType().isDOUBLE();
    }

    /**
     * Checks if this channel represents a Float
     *
     * @return true if Float, false otherwise
     */
    public boolean isFloat() {
        return pv.getType().isFLOAT();
    }

    /**
     * Checks if this channel represents a Byte
     *
     * @return true if Byte, false otherwise
     */
    public boolean isByte() {
        return pv.getType().isBYTE();
    }

    /**
     * Checks if this channel represents an Integer
     *
     * @return true if Integer, false otherwise
     */
    public boolean isInteger() {
        return pv.getType().isINT();
    }

    /**
     * Checks if this channel represents an Short
     *
     * @return true if Short, false otherwise
     */
    public boolean isShort() {
        return pv.getType().isSHORT();
    }

    /**
     * Checks if this channel represents a String
     *
     * @return true if String, false otherwise
     */
    public boolean isString() {
        return pv.getType().isSTRING();
    }

    /**
     * Checks if this channel represents an Enum
     *
     * @return true if Enum, false otherwise
     */
    public boolean isEnum() {
        return pv.getType().isENUM();
    }


    @Override
    public String getName() {
        return pv.getName();
    }

    @Override
    public void registerListener(ChannelListener<T> listener) {
        ProcessVariableEventCallback ec = new ProcessVariableEventListener(this, listener);
        eventCallbacks.put(listener, ec);
        ((ProcessVariableEventDispatcher) pv.getEventCallback()).registerEventListener(ec);
    }

    @Override
    public void unRegisterListener(ChannelListener<T> listener) {
        ((ProcessVariableEventDispatcher) pv.getEventCallback()).unregisterEventListener(eventCallbacks.get(listener));
        eventCallbacks.remove(listener);
    }

    @Override
    public void registerListener(ChannelAlarmListener<T> tChannelAlarmListener) throws CAException {
        ProcessVariableEventCallback ec = new ProcessVariableEventAlarmListener(this, tChannelAlarmListener);
        eventCallbacks.put(tChannelAlarmListener, ec);
        ((ProcessVariableEventDispatcher) pv.getEventCallback()).registerEventListener(ec);
    }

    @Override
    public void unRegisterListener(ChannelAlarmListener<T> tChannelAlarmListener) throws CAException {
        ((ProcessVariableEventDispatcher) pv.getEventCallback()).unregisterEventListener(eventCallbacks.get(tChannelAlarmListener));
        eventCallbacks.remove(tChannelAlarmListener);
    }

    @Override
    public void setValue(T value) throws CAException {
        setValue(ImmutableList.of(value));
    }

    @Override
    public void setValue(List<T> values) throws CAException {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Trying to write 0 values.");
        }
        if (!validateArgument(values)) {
            throw new IllegalArgumentException("Trying to write a " + values.get(0).getClass().getName() + " value in a " + pv.getType().getName() + " field.");
        }
        CAStatus status = pv.write(buildDBR(values), null);
        if (status != CAStatus.NORMAL) {
            throw new CAStatusException(status);
        }

    }

    @Override
    public DBR getDBR() throws CAException {
        if (pv == null) {
            throw new IllegalStateException("Channel not initialized");
        }
        DBR dbr = emptyDBR();

        CAStatus status = pv.read(dbr, null);
        if (status != CAStatus.NORMAL) {
            throw new CAStatusException(status);
        }
        return dbr;
    }

    @Override
    public List<T> getAll() throws CAException {
        return extractValues(getDBR());
    }

    @Override
    public T getFirst() throws CAException {
        return extractValues(getDBR()).get(0);
    }

    protected void setEnumLabels(String[] labels) {
        pv.setEnumLabels(labels);
    }

}
