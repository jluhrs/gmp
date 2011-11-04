package edu.gemini.epics.api;

import gov.aps.jca.dbr.DBR;

import java.util.List;

/**
 * Interface ChannelListener
 *
 * @author Nicolas A. Barriga
 *         Date: 3/16/11
 */
public interface ChannelListener<T> {
    void valueChange(String channelName, List<T> values);
}
