package edu.gemini.aspen.giapi.util.jms.status;

import edu.gemini.aspen.giapi.status.AlarmSeverity;
import edu.gemini.aspen.giapi.status.AlarmCause;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.AlarmState;
import edu.gemini.aspen.giapi.status.impl.AlarmStatus;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.util.Date;

/**
 * Specific parser to construct Alarm Status Items
 */

public abstract class AlarmStatusParser<T> extends StatusParserBase<T> {
    static AlarmSeverity[] _severities = new AlarmSeverity[3];

    static {
        _severities[0] = AlarmSeverity.ALARM_OK;
        _severities[1] = AlarmSeverity.ALARM_WARNING;
        _severities[2] = AlarmSeverity.ALARM_FAILURE;
    }

    static AlarmCause[] _causes = new AlarmCause[6];

    static {
        _causes[0] = AlarmCause.ALARM_CAUSE_OK;
        _causes[1] = AlarmCause.ALARM_CAUSE_HIHI;
        _causes[2] = AlarmCause.ALARM_CAUSE_HI;
        _causes[3] = AlarmCause.ALARM_CAUSE_LOLO;
        _causes[4] = AlarmCause.ALARM_CAUSE_LO;
        _causes[5] = AlarmCause.ALARM_CAUSE_OTHER;
    }


    StatusItem<T> buildStatusItem(String name, T value, Date timestamp, BytesMessage bm) throws JMSException {
        AlarmState alarmState = parseAlarmState(bm);
        return new AlarmStatus<>(name, value, timestamp, alarmState);
    }

    abstract public T getValue(BytesMessage bm) throws JMSException;

    private AlarmState parseAlarmState(BytesMessage bm) throws IllegalArgumentException, JMSException {

        int severityCode = bm.readUnsignedByte();
        int causeCode = bm.readUnsignedByte();
        if (severityCode < 0 || severityCode >= _severities.length)
            throw new IllegalArgumentException("No Alarm Severity associated to code " + severityCode);
        if (causeCode < 0 || causeCode >= _causes.length)
            throw new IllegalArgumentException("No Alarm Cause associated to code " + causeCode);

        boolean hasMessage = bm.readBoolean();
        String message = null;
        if (hasMessage) {
            message = bm.readUTF();
        }

        return new AlarmState(_severities[severityCode], _causes[causeCode], message);

    }
}
