package edu.gemini.cas.impl;

import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.dbr.*;

import java.util.Arrays;
import java.util.List;

/**
 * Class StringChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
class StringChannel extends AbstractChannel<String> {

    StringChannel(String name, int length) {
        super(new AlarmMemoryProcessVariable(name, null, DBR_String.TYPE, new double[length]));
    }

    @Override
    protected boolean validateArgument(List<String> values) {
        try {
            String a = (String) values.get(0);
        } catch (ClassCastException ex) {
            return false;
        }
        return isString() && (getSize() == values.size());
    }

    @Override
    protected DBR buildDBR(List<String> values) {
        return new DBR_STS_String(values.toArray(new String[0]));
    }

    @Override
    protected DBR emptyDBR() {
        return new DBR_TIME_String(getSize());
    }

    @Override
    protected List<String> extractValues(DBR dbr) {
        Object objVal = dbr.getValue();
        String[] stringVal = (String[]) objVal;
        return Arrays.asList(stringVal);
    }

    @Override
    public DBRType getType() {
        return DBR_String.TYPE;
    }
}
