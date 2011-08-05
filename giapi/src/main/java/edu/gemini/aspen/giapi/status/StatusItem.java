package edu.gemini.aspen.giapi.status;

import java.util.Date;

/**
 * Common interface for all the status items supported in the GIAPI
 */
public interface StatusItem<T> {

    /**
     * Returns the unique name for this status item
     *
     * @return name of this status item
     */
    String getName();

    /**
     * Returns the value associated to this status item
     *
     * @return the value of this status item
     */
    T getValue();

    /**
     * Returns the time at which this value was generated
     *
     * @return this item's timestamp
     */
    Date getTimestamp();

    /**
     * The accept interface for the visitor pattern
     *
     * @param visitor The Visitor to be used
     * @throws Exception in case a problem is found processing the request
     */
    void accept(StatusVisitor visitor) throws Exception;

}
