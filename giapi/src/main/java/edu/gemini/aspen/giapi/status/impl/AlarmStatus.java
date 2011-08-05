package edu.gemini.aspen.giapi.status.impl;

import edu.gemini.aspen.giapi.status.*;

import java.util.Date;

/**
 * Implementation of an Alarm Status Item.
 */
public class AlarmStatus<T> extends BasicStatus<T> implements AlarmStatusItem<T> {

    private final AlarmState _state;

    public AlarmStatus(String name, T value, AlarmState state) {
        super(name, value);
        if (state == null) {
            _state = AlarmState.DEFAULT;
        } else {
            _state = state;
        }
    }

    public AlarmStatus(String name, T value, Date timestamp, AlarmState state) {
        super(name, value, timestamp);
        if (state == null) {
            _state = AlarmState.DEFAULT;
        } else {
            _state = state;
        }
    }

    public AlarmStatus(String name, T value, AlarmSeverity severity, AlarmCause cause) {
        this(name, value, new AlarmState(severity, cause));
    }

    public AlarmStatus(String name, T value, Date timestamp, AlarmSeverity severity, AlarmCause cause) {
        this(name, value, timestamp, new AlarmState(severity, cause));
    }

    public AlarmState getAlarmState() {
        return _state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlarmStatus)) return false;
        if (!super.equals(o)) return false;

        AlarmStatus that = (AlarmStatus) o;

        return _state.equals(that._state);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + _state.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + " AlarmStatus{" +
                "state=" + _state +
                '}';
    }

    @Override
    public void accept(StatusVisitor visitor) throws Exception {
        if (visitor != null) {
            visitor.visitAlarmItem(this);
        }
    }
}
