package edu.gemini.cas.impl;

import edu.gemini.epics.api.ChannelListener;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.dbr.DBR;

/**
 * Class ProcessVariableEventListener
 *
 * @author Nicolas A. Barriga
 *         Date: 3/16/11
 */
public class ProcessVariableEventListener implements ProcessVariableEventCallback {
    private final ChannelListener<?> listener;
    private final AbstractChannel channel;

    ProcessVariableEventListener(AbstractChannel channel, ChannelListener<?> listener) {
        this.listener = listener;
        this.channel = channel;
    }

    @Override
    public void postEvent(int i, DBR dbr) {
        listener.valueChanged(channel.getName(), channel.extractValues(dbr));
    }

    @Override
    public void canceled() {
        //nothing we can do here
    }
}
