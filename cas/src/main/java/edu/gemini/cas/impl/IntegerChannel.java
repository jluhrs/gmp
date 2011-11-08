package edu.gemini.cas.impl;

import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.dbr.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class IntegerChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
class IntegerChannel extends AbstractChannel<Integer> {

    IntegerChannel(String name, int length) {
        super(new AlarmMemoryProcessVariable(name, null, DBR_Int.TYPE, new int[length]));
    }

    @Override
    protected boolean validateArgument(List<Integer> values) {
        try {
            Integer a = (Integer) values.get(0);
        } catch (ClassCastException ex) {
            return false;
        }
        return isInteger() && (getSize() == values.size());
    }

    @Override
    protected DBR buildDBR(List<Integer> values) {
        int[] newValues = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            newValues[i] = values.get(i);
        }
        return new DBR_STS_Int(newValues);
    }

    @Override
    protected DBR emptyDBR() {
        return new DBR_TIME_Int(getSize());
    }

    @Override
    protected List<Integer> extractValues(DBR dbr) {
        List<Integer> values = new ArrayList<Integer>();
        Object objVal = dbr.getValue();
        int[] intVal = (int[]) objVal;
        for (int a : intVal) {
            values.add(a);
        }
        return values;
    }

    @Override
    public DBRType getType() {
        return DBR_Int.TYPE;
    }
}
