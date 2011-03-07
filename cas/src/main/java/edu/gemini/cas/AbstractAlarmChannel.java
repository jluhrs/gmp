package edu.gemini.cas;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBR_STS_String;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;

import java.util.Arrays;
import java.util.List;

/**
 * Class AbstractAlarmChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
public class AbstractAlarmChannel<T> implements IAlarmChannel<T>{
    protected final AlarmMemoryProcessVariable alarmMessagePV;
    protected final AbstractChannel<T> ch;
    protected AbstractAlarmChannel(AbstractChannel<T> ch, AlarmMemoryProcessVariable alarmPV) {
        this.ch=ch;
        alarmMessagePV = alarmPV;
    }

    @Override
    public void setValue(T value) throws CAException {
        ch.setValue(value);
    }

    @Override
    public void setValue(List<T> values) throws CAException {
        ch.setValue(values);
    }

    @Override
    public DBR getValue() throws CAException {
        return ch.getValue();
    }

    @Override
    public String getName() {
        return ch.getName();
    }

    void register(DefaultServerImpl server){
        ch.register(server);
        server.registerProcessVaribale(alarmMessagePV.getName(), alarmMessagePV);
    }

    void destroy(DefaultServerImpl server) {
        ch.destroy(server);
        server.unregisterProcessVaribale(alarmMessagePV.getName());
        alarmMessagePV.destroy();
    }



    @Override
    public void clearAlarm() throws CAException{
        setAlarm(Status.NO_ALARM, Severity.NO_ALARM, "");
    }

    @Override
    public void setAlarm(Status status, Severity severity, String message) throws CAException{
        ch.setAlarmState(status,severity);

        ch.setValue(ch.extractValues(ch.getValue()));

        DBR_STS_String alarmMessageDbr = new DBR_STS_String(new String[]{message});
        CAStatus caStatus = alarmMessagePV.write(alarmMessageDbr, null);
        if (caStatus != CAStatus.NORMAL) {
            throw new CAStatusException(caStatus);
        }
    }
}
