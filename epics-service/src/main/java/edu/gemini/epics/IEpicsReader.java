package edu.gemini.epics;

/**
 * This interface allows reading data from a given EPICS channel.
 * Before attempting to read, the channels must be
 * connected using the {@link #bindChannel(String)} method.
 */
public interface IEpicsReader extends IEpicsBase {
    /**
     * Synchronously reads a value from the specified channel. The channel
     * must have been previously connected using the
     * {@link #bindChannel(String)} method.
     *
     * @param channelName EPICS channel to read from
     * @return the current value from the EPICS channel
     * @throws EpicsException if an exception ocurred while performing
     * the operation
     */
    Object getValue(String channelName) throws EpicsException;

}
