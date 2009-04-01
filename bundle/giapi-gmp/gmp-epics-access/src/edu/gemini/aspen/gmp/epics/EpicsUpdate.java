package edu.gemini.aspen.gmp.epics;

/**
 * Storage class that contains the  information associated to
 * an EPICS update.
 */
public class EpicsUpdate {

    private String channelName;
    private Object channelData;


    public EpicsUpdate(String channelName, Object channelData) {
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

        EpicsUpdate that = (EpicsUpdate) o;

        if (channelData != null ? !channelData.equals(that.channelData) : that.channelData != null)
            return false;
        if (channelName != null ? !channelName.equals(that.channelName) : that.channelName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = channelName != null ? channelName.hashCode() : 0;
        result = 31 * result + (channelData != null ? channelData.hashCode() : 0);
        return result;
    }
}
