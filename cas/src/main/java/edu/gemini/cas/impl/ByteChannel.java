package edu.gemini.cas.impl;

import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.dbr.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class ByteChannel
 */
class ByteChannel extends AbstractChannel<Byte> {

    ByteChannel(String name, int length) {
        super(new AlarmMemoryProcessVariable(name, null, DBR_Byte.TYPE, new byte[length]));
    }

    @Override
    protected boolean validateArgument(List<Byte> values) {
        try {
            Byte a = (Byte) values.get(0);
        } catch (ClassCastException ex) {
            return false;
        }
        return isByte() && (getSize() == values.size());
    }

    @Override
    protected DBR buildDBR(List<Byte> values) {
        byte[] newValues = new byte[values.size()];
        for (int i = 0; i < values.size(); i++) {
            newValues[i] = values.get(i).byteValue();
        }
        return new DBR_STS_Byte(newValues);
    }

    @Override
    protected DBR emptyDBR() {
        return new DBR_TIME_Byte(getSize());
    }

    @Override
    protected List<Byte> extractValues(DBR dbr) {
        List<Byte> values = new ArrayList<Byte>();
        Object objVal = dbr.getValue();
        byte[] byteVal = (byte[]) objVal;
        for (byte a : byteVal) {
            values.add(a);
        }
        return values;
    }

    @Override
    public DBRType getType() {
        return DBR_Byte.TYPE;
    }
}
