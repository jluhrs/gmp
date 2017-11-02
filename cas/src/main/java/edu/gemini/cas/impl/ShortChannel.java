package edu.gemini.cas.impl;

import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.dbr.*;

import java.util.ArrayList;
import java.util.List;

public class ShortChannel extends AbstractChannel<Short> {

    ShortChannel(String name, int length) {
        super(new AlarmMemoryProcessVariable(name, null, DBR_Short.TYPE, new short[length]));
    }

    @Override
    protected boolean validateArgument(List<Short> values) {
        try {
            Short a = (Short) values.get(0);
        } catch (ClassCastException ex) {
            return false;
        }
        return isShort() && (getSize() == values.size());
    }

    @Override
    protected DBR buildDBR(List<Short> values) {
        short[] newValues = new short[values.size()];
        for (int i = 0; i < values.size(); i++) {
            newValues[i] = values.get(i);
        }
        return new DBR_STS_Short(newValues);
    }

    @Override
    protected DBR emptyDBR() {
        return new DBR_TIME_Short(getSize());
    }

    @Override
    protected List<Short> extractValues(DBR dbr) {
        List<Short> values = new ArrayList<Short>();
        Object objVal = dbr.getValue();
        short[] shortVal = (short[]) objVal;
        for (short a : shortVal) {
            values.add(a);
        }
        return values;
    }

    @Override
    public DBRType getType() {
        return DBR_Short.TYPE;
    }
}
