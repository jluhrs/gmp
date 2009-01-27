package edu.gemini.aspen.gmp.status.api;


/**
 * A Status processor is in charge of processing a received
 * Status Item. Status Processors are executed in a separate
 * thread in the GMP.  
 */
public interface StatusProcessor {


    /**
     * Return the name of the status processor
     * @return name of the status processor
     */

    String getName();

    /**
     * Perform an operation with the status item.
     * @param item The status item to be used to perform the operation
     */
    void process(StatusItem item);

}
