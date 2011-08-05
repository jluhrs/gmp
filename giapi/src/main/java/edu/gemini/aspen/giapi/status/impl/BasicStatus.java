package edu.gemini.aspen.giapi.status.impl;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.StatusVisitor;

import java.util.Date;

/**
 * The most simple kind of Status Item. It has a name and a value.
 */
public class BasicStatus<T> implements StatusItem<T> {


    private String _name;
    private T _value;
    private Date _timestamp;

    public BasicStatus(String name, T value) {
        if (value == null) throw new NullPointerException("Can't initialize status with null value");
        if (name == null) throw new NullPointerException("Can't initialize status with null name");
        _value = value;
        _name = name;
        _timestamp = new Date();//now
    }

    public BasicStatus(String name, T value, Date timestamp) {
        if (value == null) throw new NullPointerException("Can't initialize status with null value");
        if (name == null) throw new NullPointerException("Can't initialize status with null name");
        _value = value;
        _name = name;
        _timestamp = timestamp;
    }

    public String getName() {
        return _name;
    }

    public T getValue() {
        return _value;
    }

    @Override
    public Date getTimestamp() {
        return _timestamp;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicStatus)) return false;

        BasicStatus that = (BasicStatus) o;

        return _name.equals(that._name) && _value.equals(that._value) && _timestamp.equals(that._timestamp);

    }

    @Override
    public int hashCode() {
        int result = _name.hashCode();
        result = 31 * result + _value.hashCode();
        result = 31 * result + _timestamp.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StatusItem{" +
                "name='" + _name + '\'' +
                ", value=" + _value +
                ", timestamp=" + _timestamp +
                '}';
    }

    public void accept(StatusVisitor visitor) throws Exception {
        if (visitor != null)
            visitor.visitStatusItem(this);
    }
}
