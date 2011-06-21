package edu.gemini.epics;

/**
 * Interface representing an EpicsChannel
 *
 * You should verify that the EpicsChannel is valid before using it
 * @param <T>
 */
public interface EpicsChannel<T> {
    /**
     * Indicates whether the channel is an array
     */
    boolean isArray();

    /**
     * Indicates whether the channel is valid
     */
    boolean isValid();

    /**
     * Returns the size of the array or zero if the channel is not an array
     */
    int getArraySize();

    /**
     * Returns the current value of the channel of the first array item if the channel is an array
     */
    T getValue();

    /**
     * Returns the value of an array item. If the index is higher than the length of the array the first item is returned
     */
    T getArrayValue(int index);
}
