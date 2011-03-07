package edu.gemini.cas;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import com.google.common.collect.ImmutableList;
import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.dbr.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class AbstractChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
public abstract class AbstractChannel<T> implements IChannel<T>  {
    private AlarmMemoryProcessVariable pv;

    protected AbstractChannel(AlarmMemoryProcessVariable pv) {
        this.pv = pv;
    }

    /**
     * Register this channel's process variable in the given server
     *
     * @param server the server to register the pv
     */
    void register(DefaultServerImpl server){
        server.registerProcessVaribale(pv.getName(),pv);
    }

    /**
     * Unregister this channel's process variable from the given server. Destroy the pv.
     *
     * @param server the server to unregister the pv from
     */
    void destroy(DefaultServerImpl server){
        server.unregisterProcessVaribale(getName());
        pv.destroy();
        pv=null;
    }

    void setAlarmState(Status status, Severity severity){
        pv.setStatus(status);
        pv.setSeverity(severity);
    }
    protected abstract boolean validateArgument(List<T> values);
    protected abstract DBR buildDBR(List<T> values);
    protected abstract DBR emptyDBR();
    protected abstract List<T> extractValues(DBR dbr);
    protected int getSize(){
        return pv.getDimensionSize(0);
    }

    /**
     * Checks if this channel represents a Double
     *
     * @return true if Double, false otherwise
     */
    public boolean isDouble(){
        return pv.getType().isDOUBLE();
    }

    /**
      * Checks if this channel represents a Float
      *
      * @return true if Float, false otherwise
      */
    public boolean isFloat(){
        return pv.getType().isFLOAT();
    }

    /**
      * Checks if this channel represents an Integer
      *
      * @return true if Integer, false otherwise
      */
    public boolean isInteger(){
        return pv.getType().isINT();
    }

    /**
      * Checks if this channel represents a String
      *
      * @return true if String, false otherwise
      */
    public boolean isString(){
        return pv.getType().isSTRING();
    }

    @Override
    public String getName(){
        return pv.getName();
    }

    @Override
    public void setValue(T value) throws CAException {
        setValue(ImmutableList.of(value));
    }
    @Override
    public void setValue(List<T> values) throws CAException {
        if (!validateArgument(values)) {
            throw new IllegalArgumentException("Trying to write a " + values.get(0).getClass().getName() + " value in a " + pv.getType().getName() + " field.");
        }
        CAStatus status = pv.write(buildDBR(values), null);
        if (status != CAStatus.NORMAL) {
            throw new CAStatusException(status);
        }

    }

    @Override
    public DBR getValue() throws CAException {
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
}
