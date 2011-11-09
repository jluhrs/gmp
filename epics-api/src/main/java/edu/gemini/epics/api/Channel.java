package edu.gemini.epics.api;

import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;

import java.util.List;

/**
 * Interface Channel
 *
 * @author Nicolas A. Barriga
 *         Date: Dec 2, 2010
 */
public interface Channel<T> extends ReadOnlyChannel<T> {
    /**
     * Sets a new value to a PV
     *
     * @param value the value to set the PV to
     * @throws CAException              if write couldn't be completed
     * @throws IllegalArgumentException if wrong data type or incorrect amount of data is passed.
     */
    void setValue(T value) throws CAException, TimeoutException;

    void setValue(List<T> values) throws CAException, TimeoutException;

}
