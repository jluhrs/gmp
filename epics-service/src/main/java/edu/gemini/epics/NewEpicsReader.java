package edu.gemini.epics;

/**
 * Interface to access an epics channel
 *
 * The implementation takes care of bind/unbind of the channel
 */
public interface NewEpicsReader {
    /**
     * Returns a representation of a channel. It is recommended not to hold instances of
     * EpicsChannel as channels can disappear anytime
     *
     * @param channelName The name of the channel
     * @param <T> Type of the underlying channel
     * @return An EpicsChannel representation of a channel or a NullEpicsChannel if the channel is not available
     */
    <T> EpicsChannel<T> getChannel(String channelName);

    /**
     * Returns a representation of a channel containing an array. It is recommended not to hold instances of
     * EpicsChannel as channels can disappear anytime
     *
     * @param channelName The name of the channel
     * @param <T> Type of the underlying channel
     * @return An EpicsChannel representation of a channel or a NullEpicsChannel if the channel is not available
     */
    <T> EpicsChannelArray<T> getArrayChannel(String channelName);
}
