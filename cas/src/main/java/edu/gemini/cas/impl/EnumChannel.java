package edu.gemini.cas.impl;

import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.dbr.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class EnumChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/16/11
 */
class EnumChannel<T extends Enum<T>> extends AbstractChannel<T> {
    final private Class<T> clazz;

    EnumChannel(String name, int length, Class<T> clazz) {
        super(new AlarmMemoryProcessVariable(name, null, DBR_Enum.TYPE, new short[length]));
        this.clazz = clazz;
        List<String> enumConstants = new ArrayList<String>();
        for (Enum a : clazz.getEnumConstants()) {
            enumConstants.add(a.name());
        }
        setEnumLabels(enumConstants.toArray(new String[0]));
    }

    @Override
    protected boolean validateArgument(List<T> values) {
        try {
            Enum<T> a = (Enum<T>) values.get(0);
            if (!clazz.equals(values.get(0).getClass())) {
                return false;
            }
        } catch (ClassCastException ex) {
            return false;
        }
        return isEnum() && (getSize() == values.size());
    }

    @Override
    protected DBR buildDBR(List<T> values) {
        short[] newValues = new short[values.size()];
        for (Enum value : values) {
            newValues[0] = (short) value.ordinal();
        }
        return new DBR_LABELS_Enum(newValues);
    }

    @Override
    protected DBR emptyDBR() {
        return new DBR_TIME_LABELS_Enum(getSize());
    }

    @Override
    protected List<T> extractValues(DBR dbr) {
        List<T> values = new ArrayList<T>();
        Object objVal = dbr.getValue();
        short[] doubleVal = (short[]) objVal;
        for (short a : doubleVal) {
            values.add(clazz.getEnumConstants()[a]);
        }
        return values;
    }

    Class<T> getEnumClass() {
        return clazz;
    }

    @Override
    public DBRType getType() {
        return DBR_Enum.TYPE;
    }
}
