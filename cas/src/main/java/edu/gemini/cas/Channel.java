package edu.gemini.cas;

import com.cosylab.epics.caj.cas.util.MemoryProcessVariable;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.dbr.*;

class Channel implements IChannel {
    private MemoryProcessVariable pv;

    /*package private constructor*/
    Channel(MemoryProcessVariable pv) {
        this.pv = pv;
    }

    DBRType getType(){
        return pv.getType();
    }

    @Override
    public String getName(){
        return pv.getName();
    }

    void destroy(){
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

        if (!pv.getType().isINT()) {
            throw new IllegalArgumentException("Trying to write a " + values[0].getClass().getName() + " value in a " + pv.getType().getName() + " field.");
        }
        int[] newValues = new int[values.length];

        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i];
        }
        CAStatus status = pv.write(new DBR_Int(newValues), null);
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

        if (!pv.getType().isFLOAT()) {
            throw new IllegalArgumentException("Trying to write a " + values[0].getClass().getName() + " value in a " + pv.getType().getName() + " field.");
        }
        float[] newValues = new float[values.length];

        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i];
        }
        CAStatus status = pv.write(new DBR_Float(newValues), null);
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

        if (!pv.getType().isDOUBLE()) {
            throw new IllegalArgumentException("Trying to write a " + values[0].getClass().getName() + " value in a " + pv.getType().getName() + " field.");
        }
        double[] newValues = new double[values.length];

        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i];
        }
        CAStatus status = pv.write(new DBR_Double(newValues), null);
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

        if (!pv.getType().isSTRING()) {
            throw new IllegalArgumentException("Trying to write a " + values[0].getClass().getName() + " value in a " + pv.getType().getName() + " field.");
        }

        CAStatus status = pv.write(new DBR_String(values), null);
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
        if (pv.getType().isINT()) {
            dbr = new DBR_TIME_Int(pv.getDimensionSize(0));
        } else if (pv.getType().isFLOAT()) {
            dbr = new DBR_TIME_Float(pv.getDimensionSize(0));
        } else if (pv.getType().isDOUBLE()) {
            dbr = new DBR_TIME_Double(pv.getDimensionSize(0));
        } else if (pv.getType().isSTRING()) {
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
