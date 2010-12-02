package edu.gemini.cas;

import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;

/**
 * Interface IChannel
 *
 * @author Nicolas A. Barriga
 *         Date: Dec 2, 2010
 */
public interface IChannel {
    /**
     * Sets a new value to a PV
     * @param value the value to set the PV to
     * @throws CAException if write couldn't be completed
     * @throws IllegalArgumentException if wrong data type or incorrect amount of data is passed.
     */
    void setValue(Integer value) throws CAException;

    void setValue(Integer[] values) throws CAException;

    void setValue(Float value) throws CAException;

    void setValue(Float[] values) throws CAException;

    void setValue(Double value) throws CAException;

    void setValue(Double[] values) throws CAException;

    void setValue(String value) throws CAException;

    void setValue(String[] values) throws CAException;

    /**
     * Retrieves a value from a PV
     *
     * @return a DBR containing the value(s) of the process variable
     * @throws CAException if read couldn't be completed
     * @throws IllegalStateException if Channel is not correctly initialized
     */
    DBR getValue() throws CAException;

    String getName();
}
