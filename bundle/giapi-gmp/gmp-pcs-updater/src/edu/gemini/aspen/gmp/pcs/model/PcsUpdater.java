package edu.gemini.aspen.gmp.pcs.model;

/**
 * An interface to define objects that will receive
 * {@link edu.gemini.aspen.gmp.pcs.model.PcsUpdate} notifications
 * and will perform an operation with it. For instance,
 * an implementation of this interface can take the update information
 * and send it to Gemini via EPICS channel access. Alternative implementations
 * can be used for logging or testing.
 *
 */
public interface PcsUpdater {
    /**
     * Process the given update
     * @param update the update to process.
     * @throws PcsUpdaterException in case there is a problem processing the update
     */
     void update(PcsUpdate update) throws PcsUpdaterException;

}
