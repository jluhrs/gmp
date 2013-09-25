package edu.gemini.cas.impl;

import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.dbr.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class DoubleChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
class DoubleChannel extends AbstractChannel<Double> {

    DoubleChannel(String name, int length) {
        super(new AlarmMemoryProcessVariable(name, null, DBR_Double.TYPE, new double[length]));
    }

    @Override
    protected boolean validateArgument(List<Double> values) {
        try {
            Double a = (Double) values.get(0);
        } catch (ClassCastException ex) {
            return false;
        }
        return isDouble() && (getSize() == values.size());
    }

    @Override
    protected DBR buildDBR(List<Double> values) {
        double[] newValues = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            newValues[i] = values.get(i).doubleValue();
        }
        return new DBR_STS_Double(newValues);
    }

    @Override
    protected DBR emptyDBR() {
        return new DBR_TIME_Double(getSize());
    }

    @Override
    protected List<Double> extractValues(DBR dbr) {
        List<Double> values = new ArrayList<Double>();
        Object objVal = dbr.getValue();
        double[] doubleVal = (double[]) objVal;
        for (double a : doubleVal) {
            values.add(a);
        }
        return values;
    }

    @Override
    public DBRType getType() {
        return DBR_Double.TYPE;
    }
}
