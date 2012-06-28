package edu.gemini.cas.impl;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import edu.gemini.cas.AlarmChannel;
import edu.gemini.epics.api.ChannelAlarmListener;
import edu.gemini.epics.api.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;

import java.util.List;

/**
 * Class AbstractAlarmChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
class AbstractAlarmChannel<T> implements AlarmChannel<T> {
    private final String ALARM_MESSAGE_SUFFIX = ".OMSS";
    protected final AbstractChannel<String> alarmCh;
    protected final AbstractChannel<T> ch;
    boolean registered = false;

    protected AbstractAlarmChannel(AbstractChannel<T> ch) {
        this.ch = ch;
        alarmCh = new StringChannel(ch.getName() + ALARM_MESSAGE_SUFFIX, 1);
    }

    @Override
    public boolean isValid() {
        return registered;
    }

    @Override
    public DBRType getType() {
        return ch.getType();
    }

    @Override
    public void registerListener(ChannelListener<T> listener) {
        ch.registerListener(listener);
    }

    @Override
    public void unRegisterListener(ChannelListener<T> listener) {
        ch.unRegisterListener(listener);
    }

    @Override
    public void registerListener(ChannelAlarmListener<T> tChannelAlarmListener) throws CAException {
        ch.registerListener(tChannelAlarmListener);
    }

    @Override
    public void unRegisterListener(ChannelAlarmListener<T> tChannelAlarmListener) throws CAException {
        ch.unRegisterListener(tChannelAlarmListener);
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
    public DBR getDBR() throws CAException {
        return ch.getDBR();
    }

    @Override
    public List<T> getAll() throws CAException {
        return ch.getAll();
    }

    @Override
    public T getFirst() throws CAException {
        return ch.getFirst();
    }

    @Override
    public String getName() {
        return ch.getName();
    }

    void register(DefaultServerImpl server) {
        ch.register(server);
        alarmCh.register(server);
        registered = true;
    }

    void destroy(DefaultServerImpl server) {
        ch.destroy(server);
        alarmCh.destroy(server);
        registered = false;
    }

    @Override
    public void clearAlarm() throws CAException {
        setAlarm(Status.NO_ALARM, Severity.NO_ALARM, "");
    }

    @Override
    public void setAlarm(Status status, Severity severity, String message) throws CAException {
        ch.setAlarmState(status, severity);

        ch.setValue(ch.extractValues(ch.getDBR()));

        alarmCh.setValue(message != null ? message : "");
    }
}
