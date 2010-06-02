package edu.gemini.aspen.giapi.commands;

/**
 * Defines a handler to update the OCS with completion information
 * for sequence commands that do not complete immediately.
 */
public interface CommandUpdater {

    /**
     * Updates the OCS with completion information for the given action Id.
     *
     * @param actionId the action id being updated
     * @param response completion information associated to the action Id.
     */
    void updateOcs(int actionId, HandlerResponse response);
}
