package edu.gemini.epics;

/**
 * Interface representing an EpicsChannel
 *
 * You should verify that the EpicsChannel is valid before using it
 * @param <T>
 */
public interface EpicsChannelArray<T> {
    /**
     * Indicates whether the channel is valid
     */
    boolean isValid();

    /**
     * Returns the current value of the channel of the first array item if the channel is an array
     */
    T[] getValue();
}
