package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.gmp.commands.HandlerResponse;

/**
 * This interface defines a method to dispatch an action using
 * a given {@link edu.gemini.aspen.gmp.commands.model.ActionSender}. The
 * operation returns a {@link edu.gemini.aspen.gmp.commands.HandlerResponse}
 * representing the resut of the action.
 */
public interface SequenceCommandExecutor {

    /**
     * Use the given sender to send the action and obtain a response
     *
     * @param action action to be sent
     * @param sender sender that will be used to dispatch the action
     * @return response to the sequence command described in the
     *         action.
     */
    HandlerResponse execute(Action action, ActionSender sender);

}
