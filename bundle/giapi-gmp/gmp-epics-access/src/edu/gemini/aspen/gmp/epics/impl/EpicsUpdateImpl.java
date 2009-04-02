package edu.gemini.aspen.gmp.epics.impl;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;

/**
 * Storage class that contains the  information associated to
 * an EPICS update.
 */
public class EpicsUpdateImpl implements EpicsUpdate {

    private String channelName;
    private Object channelData;


    public EpicsUpdateImpl(String channelName, Object channelData) {
        this.channelName = channelName;
        this.channelData = channelData;
    }

    public String getChannelName() {
        return channelName;
    }

    public Object getChannelData() {
        return channelData;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EpicsUpdateImpl that = (EpicsUpdateImpl) o;

        if (channelData != null ? !channelData.equals(that.channelData) : that.channelData != null)
            return false;
        if (channelName != null ? !channelName.equals(that.channelName) : that.channelName != null)
            return false;
        //everything is fine, the objects are the same
        return true;
    }

    @Override
    public int hashCode() {
        int result = channelName != null ? channelName.hashCode() : 0;
        result = 31 * result + (channelData != null ? channelData.hashCode() : 0);
        return result;
    }
}
