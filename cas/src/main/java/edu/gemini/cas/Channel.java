package edu.gemini.cas;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.dbr.*;

/**
 * Class Channel
 *
 * @author Nicolas A. Barriga
 *         Date: 1/17/11
 */
class Channel implements IChannel{
    protected AlarmMemoryProcessVariable pv;

    Channel(AlarmMemoryProcessVariable pv) {
        this.pv = pv;
    }

    /**
     * Register this channel's process variable in the given server
     *
     * @param server the server to register the pv
     */
    public void register(DefaultServerImpl server){
        server.registerProcessVaribale(pv.getName(),pv);
    }

    /**
     * Checks if this channel represents a Double
     *
     * @return true if Double, false otherwise
     */
    boolean isDouble(){
        return pv.getType().isDOUBLE();
    }

    /**
      * Checks if this channel represents a Float
      *
      * @return true if Float, false otherwise
      */
    boolean isFloat(){
        return pv.getType().isFLOAT();
    }

    /**
      * Checks if this channel represents an Integer
      *
      * @return true if Integer, false otherwise
      */
    boolean isInteger(){
        return pv.getType().isINT();
    }

    /**
      * Checks if this channel represents a String
      *
      * @return true if String, false otherwise
      */
    boolean isString(){
        return pv.getType().isSTRING();
    }

    @Override
    public String getName(){
        return pv.getName();
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

    @Override
    public void setValue(Integer value) throws CAException {
        setValue(new Integer[]{value});
    }

    @Override
    public void setValue(Integer[] values) throws CAException {
        if (pv.getDimensionSize(0) != values.length) {
            throw new IllegalArgumentException("Incorrect number of values. Expected: " + pv.getDimensionSize(0) + ", got: " + values.length);
        }

        if (!isInteger()) {
            throw new IllegalArgumentException("Trying to write a " + values[0].getClass().getName() + " value in a " + pv.getType().getName() + " field.");
        }
        int[] newValues = new int[values.length];

        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i];
        }
        DBR_STS_Int dbr = new DBR_STS_Int(newValues);
        CAStatus status = pv.write(dbr, null);
        if (status != CAStatus.NORMAL) {
            throw new CAStatusException(status);
        }

    }

    @Override
    public void setValue(Float value) throws CAException {
        setValue(new Float[]{value});
    }

    @Override
    public void setValue(Float[] values) throws CAException {
        if (pv.getDimensionSize(0) != values.length) {
            throw new IllegalArgumentException("Incorrect number of values. Expected: " + pv.getDimensionSize(0) + ", got: " + values.length);
        }

        if (!isFloat()) {
            throw new IllegalArgumentException("Trying to write a " + values[0].getClass().getName() + " value in a " + pv.getType().getName() + " field.");
        }
        float[] newValues = new float[values.length];

        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i];
        }
        DBR_STS_Float dbr = new DBR_STS_Float(newValues);
        CAStatus status = pv.write(dbr, null);
        if (status != CAStatus.NORMAL) {
            throw new CAStatusException(status);
        }

    }

    @Override
    public void setValue(Double value) throws CAException {
        setValue(new Double[]{value});
    }

    @Override
    public void setValue(Double[] values) throws CAException {
        if (pv.getDimensionSize(0) != values.length) {
            throw new IllegalArgumentException("Incorrect number of values. Expected: " + pv.getDimensionSize(0) + ", got: " + values.length);
        }

        if (!isDouble()) {
            throw new IllegalArgumentException("Trying to write a " + values[0].getClass().getName() + " value in a " + pv.getType().getName() + " field.");
        }
        double[] newValues = new double[values.length];

        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i];
        }
        DBR_STS_Double dbr = new DBR_STS_Double(newValues);
        CAStatus status = pv.write(dbr, null);
        if (status != CAStatus.NORMAL) {
            throw new CAStatusException(status);
        }

    }

    @Override
    public void setValue(String value) throws CAException {
        setValue(new String[]{value});
    }

    @Override
    public void setValue(String[] values) throws CAException {
        if (pv.getDimensionSize(0) != values.length) {
            throw new IllegalArgumentException("Incorrect number of values. Expected: " + pv.getDimensionSize(0) + ", got: " + values.length);
        }

        if (!isString()) {
            throw new IllegalArgumentException("Trying to write a " + values[0].getClass().getName() + " value in a " + pv.getType().getName() + " field.");
        }
        DBR_STS_String dbr = new DBR_STS_String(values);
        CAStatus status = pv.write(dbr, null);
        if (status != CAStatus.NORMAL) {
            throw new CAStatusException(status);
        }

    }

    @Override
    public DBR getValue() throws CAException {
        if (pv == null) {
            throw new IllegalStateException("Channel not initialized");
        }
        DBR dbr;
        if (isInteger()) {
            dbr = new DBR_TIME_Int(pv.getDimensionSize(0));
        } else if (isFloat()) {
            dbr = new DBR_TIME_Float(pv.getDimensionSize(0));
        } else if (isDouble()) {
            dbr = new DBR_TIME_Double(pv.getDimensionSize(0));
        } else if (isString()) {
            dbr = new DBR_TIME_String(pv.getDimensionSize(0));
        } else {
            throw new IllegalStateException("Channel incorrectly initialized");
        }
        CAStatus status = pv.read(dbr, null);
        if (status != CAStatus.NORMAL) {
            throw new CAStatusException(status);
        }
        return dbr;
    }
}
