package edu.gemini.epics.api;

import gov.aps.jca.dbr.DBR;

/**
 * Interface ChannelListener
 *
 * @author Nicolas A. Barriga
 *         Date: 3/16/11
 */
public interface ChannelListener {
    void valueChange(DBR dbr);
}
