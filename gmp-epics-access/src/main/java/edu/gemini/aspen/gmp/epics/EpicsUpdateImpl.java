package edu.gemini.aspen.gmp.epics;

import java.util.List;

/**
 * A simple implementation for an Epics Update.
 */
public class EpicsUpdateImpl<T> implements EpicsUpdate<T> {
    private final String channelName;
    private final List<T> channelData;

    public EpicsUpdateImpl(String channelName, List<T> channelData) {
        if (channelName == null) {
            throw new IllegalArgumentException("Channel name cannot be null");
        }
        if (channelData == null) {
            throw new IllegalArgumentException("Channel data cannot be null");
        }
        this.channelName = channelName;
        this.channelData = channelData;
    }

    public String getChannelName() {
        return channelName;
    }

    public List<T> getChannelData() {
        return channelData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EpicsUpdateImpl that = (EpicsUpdateImpl) o;

        if (!channelData.equals(that.channelData)) {
            return false;
        }
        if (!channelName.equals(that.channelName)) {
            return false;
        }
        //everything is fine, the objects are the same
        return true;
    }

    @Override
    public int hashCode() {
        int result = channelName.hashCode();
        result = 31 * result + channelData.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EpicsUpdate{" +
                "channelName='" + channelName + '\'' +
                ", channelData=" + channelData +
                '}';
    }
}
