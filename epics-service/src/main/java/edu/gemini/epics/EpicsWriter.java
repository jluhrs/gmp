package edu.gemini.epics;

/**
 * This interface allows writing data to a given EPICS channel.
 * Before attempting to write, the channels must be
 * connected using the {@link #bindChannel(String)} method.
 */
public interface EpicsWriter extends EpicsBase {

    /**
     * Synchronously writes a double to the specified channel. The channel
     * must have been previously connected using the @link #bindChannel(String)} method.
     * 
     * @param channel EPICS channel to write to
     * @param value the value to write to the channel
     * @throws EpicsException if an exception happened while performing the operation
     */
    public void write(String channel, Double value) throws EpicsException;


    /**
     * Synchronously writes an array of Doubles to the specified channel. The channel
     * must have been previously connected using the @link #bindChannel(String)} method.
     *
     * @param channel EPICS channel to write to
     * @param value the values to write to the channel
     * @throws EpicsException if an exception happened while performing the operation
     */
    public void write(String channel, Double[] value) throws EpicsException;


    /**
     * Synchronously writes an array of doubles to the specified channel. The channel
     * must have been previously connected using the @link #bindChannel(String)} method.
     * 
     * @param channel EPICS channel to write to
     * @param value the values to write to the channel
     * @throws EpicsException if an exception happened while performing the operation
     */
    public void write(String channel, double[] value) throws EpicsException;



}
