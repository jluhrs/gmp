package edu.gemini.cas.impl;

import edu.gemini.epics.api.ChannelAlarmListener;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;

/**
 * Class ProcessVariableEventListener
 *
 * @author Nicolas A. Barriga
 *         Date: 3/16/11
 */
public class ProcessVariableEventAlarmListener implements ProcessVariableEventCallback {
    private final ChannelAlarmListener<?> listener;
    private final AbstractChannel channel;

    ProcessVariableEventAlarmListener(AbstractChannel channel, ChannelAlarmListener<?> listener) {
        this.listener = listener;
        this.channel = channel;
    }

    @Override
    public void postEvent(int i, DBR dbr) {
        listener.valueChanged(channel.getName(),
                channel.extractValues(dbr),
                dbr.isSTS() ? ((STS) dbr).getStatus() : Status.NO_ALARM,
                dbr.isSTS() ? ((STS) dbr).getSeverity() : Severity.NO_ALARM);
    }

    @Override
    public void canceled() {
        //nothing we can do here
    }
}
