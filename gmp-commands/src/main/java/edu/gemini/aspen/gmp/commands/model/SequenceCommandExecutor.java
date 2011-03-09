package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.HandlerResponse;

/**
 * This interface defines a method to dispatch an action using
 * a given {@link edu.gemini.aspen.gmp.commands.model.ActionSender}. The
 * operation returns a {@link edu.gemini.aspen.giapi.commands.HandlerResponse}
 * representing the result of the action.
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
