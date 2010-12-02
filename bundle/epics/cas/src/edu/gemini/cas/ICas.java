package edu.gemini.cas;

import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;

/**
 * Interface ICas
 *
 * @author Nicolas A. Barriga
 *         Date: Nov 29, 2010
 */
public interface ICas {
    /**
     * Adds a new process variable.
     *
     * @param name name of the PV
     * @param firstValue value to set the PV to
     * @param remainingValues optional remaining values if the PV is an array
     * @param <T> Must be one of Integer, Double, Float or String
     * @throws IllegalArgumentException if the values are of an unsupported type, or the values are not all of the same type
     */
    public <T> void addVariable(String name, T firstValue, T... remainingValues);


    /**
     * Removes a process variable
     *
     * @param name name of the PV to remove
     */
    //TODO:what do we do if the variable is not registered? ignore? return a boolean? throw exception?
    public void removeVariable(String name);

    /**
     * Changes the value of an existing PV
     * 
     * @param name name of the PV
     * @param firstValue value to set the PV to
     * @param remainingValues optional remaining values if the PV is an array
     * @param <T> Must be one of Integer, Double, Float or String
     * @throws CAException if a Channel Exception occured while performing this operation.
     * @throws java.lang.IllegalArgumentException if the variable name is not registered, or
     *          a wrong data type is passed, or the incorrect amount of data is passed
     */
    public <T> void put(String name, T firstValue, T... remainingValues)throws CAException;

    //We could implement this to avoid array creation as most of our PVs will have length==1
    //public <T> void put(String name, T value)throws CAException;

    /**
     *
     * @param name name of the PV
     * @return DBR containing the reply
     * @throws CAException if a Channel Exception occured while performing this operation.
     * @throws IllegalStateException if the channel is in no state to perform this operation (ie destroyed, etc...)
     * @throws java.lang.IllegalArgumentException if the variable name is not registered
     */
    public DBR get(String name) throws CAException;
}
