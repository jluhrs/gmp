package edu.gemini.aspen.giapi.status;

/**
 * Interface to handle status reception. 
 */

public interface StatusHandler {

    /**
     * Return a unique, immutable name for this <code>StatusHandler</code>
     * @return name of the <code>StatusHandler</code>
     */
    String getName();

    /**
     * Alert listeners that a status update has occurred.
     * @param item the status item in this update
     */
    <T> void update(StatusItem<T> item);

}
