package edu.gemini.cas.impl;

import edu.gemini.cas.ChannelListener;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.dbr.DBR;

/**
 * Class ProcessVariableEventListener
 *
 * @author Nicolas A. Barriga
 *         Date: 3/16/11
 */
public class ProcessVariableEventListener implements ProcessVariableEventCallback {
    private final ChannelListener listener;

    ProcessVariableEventListener(ChannelListener listener) {
        this.listener = listener;
    }

    @Override
    public void postEvent(int i, DBR dbr) {
        listener.valueChange(dbr);
    }

    @Override
    public void canceled() {
        //nothing we can do here
    }
}
