package edu.gemini.epics;

import edu.gemini.epics.api.ReadOnlyChannel;

/**
 * Interface to access an epics channel
 * <p/>
 * The implementation takes care of bind/unbind of the channel
 */
public interface NewEpicsReader {
    /**
     * Returns a representation of a channel. It is recommended not to hold instances of
     * EpicsChannel as channels can disappear anytime
     *
     * @param channelName The name of the channel
     * @param <T>         Type of the underlying channel
     * @return An EpicsChannel representation of a channel or a NullEpicsChannel if the channel is not available
     */
    <T> ReadOnlyChannel<T> getChannel(String channelName);
}
