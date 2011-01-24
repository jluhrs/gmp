package edu.gemini.cas;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.dbr.*;

class AlarmChannel extends Channel implements IAlarmChannel {
    protected final AlarmMemoryProcessVariable alarmMessagePV;

    /*package private constructor*/
    AlarmChannel(AlarmMemoryProcessVariable pv, AlarmMemoryProcessVariable alarmPV) {
        super(pv);
        alarmMessagePV = alarmPV;
    }

    @Override
    public void register(DefaultServerImpl server){
        super.register(server);
        server.registerProcessVaribale(alarmMessagePV.getName(), alarmMessagePV);
    }

    @Override
    public void destroy(DefaultServerImpl server) {
        super.destroy(server);
        server.unregisterProcessVaribale(alarmMessagePV.getName());
        alarmMessagePV.destroy();
    }

    @Override
    public void clearAlarm() throws CAException{
        pv.setStatus(0);
        pv.setSeverity(0);
        CAStatus caStatus= pv.write(getValue(), null);
        if (caStatus != CAStatus.NORMAL) {
            throw new CAStatusException(caStatus);
        }
        DBR_STS_String alarmMessageDbr = new DBR_STS_String(new String[]{""});
        caStatus = alarmMessagePV.write(alarmMessageDbr, null);
        if (caStatus != CAStatus.NORMAL) {
            throw new CAStatusException(caStatus);
        }
    }

    @Override
    public void setAlarm(Status status, Severity severity, String message) throws CAException{
        pv.setStatus(status);
        pv.setSeverity(severity);
        CAStatus caStatus= pv.write(getValue(), null);
        if (caStatus != CAStatus.NORMAL) {
            throw new CAStatusException(caStatus);
        }
        DBR_STS_String alarmMessageDbr = new DBR_STS_String(new String[]{message});
        caStatus = alarmMessagePV.write(alarmMessageDbr, null);
        if (caStatus != CAStatus.NORMAL) {
            throw new CAStatusException(caStatus);
        }
    }

}
