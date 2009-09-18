package edu.gemini.epics;

/**
 * This interface allows writting data to a given EPICS channel.
 * Before attempting to write, the channels must be
 * connected using the {@link #bindChannel(String)} method.
 */
public interface IEpicsWriter {

    /**
     * Makes the connection of the specified channel name with an actual
     * EPICS channel.
     * @param channel the channel to connect to
     * @throws EpicsException in case there is a problem connecting to the
     * given channel.
     */
    public void bindChannel(String channel) throws EpicsException;


    /**
     * Synchronously writes a double to the specified channel. The channel
     * must have been previously connected using the @link #bindChannel(String)} method.
     * @param channel EPICS channel to write to
     * @param value the value to write to the channel
     * @throws EpicsException if an exception ocurred while performing the operation
     */
    public void write(String channel, Double value) throws EpicsException;


    /**
     * Synchronously writes an array of Doubles to the specified channel. The channel
     * must have been previously connected using the @link #bindChannel(String)} method.
     * @param channel EPICS channel to write to
     * @param value the values to write to the channel
     * @throws EpicsException if an exception ocurred while performing the operation
     */

    public void write(String channel, Double[] value) throws EpicsException;


    /**
     * Synchronously writes an array of doubles to the specified channel. The channel
     * must have been previously connected using the @link #bindChannel(String)} method.
     * @param channel EPICS channel to write to
     * @param value the values to write to the channel
     * @throws EpicsException if an exception ocurred while performing the operation
     */
    public void write(String channel, double[] value) throws EpicsException;



}
