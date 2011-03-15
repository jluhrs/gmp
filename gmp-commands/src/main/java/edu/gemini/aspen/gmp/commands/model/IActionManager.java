package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.giapi.commands.HandlerResponse;

/**
 * Created by IntelliJ IDEA.
 * User: cquiroz
 * Date: 3/15/11
 * Time: 5:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IActionManager {
    /**
     * Register that there is a future {@link edu.gemini.aspen.giapi.commands.HandlerResponse}
     * to be received for the given APPLY action. If the action is not an APPLY, this method does nothing.
     *
     * @param action the APPLY action that will get a future response later.
     */
    void increaseRequiredResponses(Action action);

    /**
     * Register this Action to keep track its progress internally. When
     * the completion information associated to this action is available, the
     * listener contained in it will be invoked.
     *
     * @param action the action to register
     */
    void registerAction(Action action);

    /**
     * Deregister the given action from the monitored actions. This
     * is done so the system don't attempt to update with completion
     * information those actions that complete immediately.
     * @param action the action to deregister.
     */
    void unregisterAction(Action action);

    /**
     * Register the completion information to be sent to the clients identified
     * by the given action Id. This information is queued and used by the
     * processing thread to notify the clients.
     *
     * @param actionId the action Id used to identify the clients waiting for
     *                 completion feedback
     * @param response the completion information to be sent to the clients.
     */

    void registerCompletionInformation(int actionId,
                                       HandlerResponse response);

    void lock();

    void unlock();
}
