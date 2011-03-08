package edu.gemini.cas.impl;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import edu.gemini.cas.IAlarmChannel;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;

import java.util.List;

/**
 * Class AbstractAlarmChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
class AbstractAlarmChannel<T> implements IAlarmChannel<T> {
    private final String ALARM_MESSAGE_SUFFIX=".OMSS";
    protected final AbstractChannel<String> alarmCh;
    protected final AbstractChannel<T> ch;
    protected AbstractAlarmChannel(AbstractChannel<T> ch) {
        this.ch=ch;
        alarmCh = new StringChannel(ch.getName()+ALARM_MESSAGE_SUFFIX, 1);
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
        alarmCh.register(server);
    }

    void destroy(DefaultServerImpl server) {
        ch.destroy(server);
        alarmCh.destroy(server);
    }

    @Override
    public void clearAlarm() throws CAException{
        setAlarm(Status.NO_ALARM, Severity.NO_ALARM, "");
    }

    @Override
    public void setAlarm(Status status, Severity severity, String message) throws CAException{
        ch.setAlarmState(status,severity);

        ch.setValue(ch.extractValues(ch.getValue()));

        alarmCh.setValue(message!=null?message:"");
    }
}
