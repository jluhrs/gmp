package edu.gemini.aspen.gmp.status.api;

/**
 * Common interface for all the status items supported in the GIAPI
 */
public interface StatusItem {

    /**
     * Returns the unique name for this status item
     * @return name of this status item
     */
    String getName();

    /**
     * Returns the value associated to this status item
     * @return the value of this status item
     */
    Object getValue();

}
