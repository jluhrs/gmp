package edu.gemini.aspen.gmp.broker.commands;

import edu.gemini.aspen.gmp.commands.api.*;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class allows to keep track of the actions that are being sent to the
 * instruments so the system can later receive completion information and notify
 * the interested parties.
 */
public class ActionIdManager {

    private static final Logger LOG = Logger.getLogger(
            ActionIdManager.class.getName());

    private final Queue<ActionId> _actionQueue = new ConcurrentLinkedQueue<ActionId>();


    /**
     * Register this sequence command as an outstanding sequence command. When
     * the completion information of this sequence command is received, the
     * listener provided will be invoked.
     *
     * @param command  Sequence command that will be registered
     * @param activity Activiy associated to the sequence command
     * @param config   Configuration, if any, associated to the sequence
     *                 command. Can be <code>null</code>
     * @param listener handler that will be invoked when completion information
     *                 is received for this sequence command. Can be
     *                 <code>null</code>
     *
     * @return The Action ID associated to this sequence command.
     */
    public ActionId registerCommand(SequenceCommand command,
                                    Activity activity,
                                    Configuration config,
                                    CompletionListener listener) {
        ActionId actionId = new ActionId(command, activity, config, listener);
        _actionQueue.add(actionId);
        LOG.info(
                "Registering new command " + command + " with action id " + actionId
                        .getActionId());
        return actionId;
    }

    /**
     * Update the clients waiting for completion information associated to the
     * given Action Id.
     *
     * @param actionId the action Id used to identify the clients waiting for
     *                 completion feedback
     * @param response the completion information to be sent to the clients.
     */
    public void updateAction(int actionId, HandlerResponse response) {
        LOG.info("Updating action id " + actionId);
        synchronized (_actionQueue) {

            ActionId id = _actionQueue.peek();

            if (id == null) {
                LOG.info(
                        "I don't know about action ID " + actionId + ". Usually this means a problem in the instrument code.");
                return;
            }
            //If the first action id in the queue is bigger
            //than the one received, we don't have anything to do. Log this
            //since it's an indication something weird is happening
            if (id.getActionId() > actionId) {
                LOG.log(Level.SEVERE,
                        "Action ID received " + actionId + " but we don't have issued that action yet");
                return;
            }

            while (id != null && id.getActionId() <= actionId) {
                LOG.info("Notifying listeners for action ID " + id);
                CompletionListener listener = id.getCompletionListener();
                if (listener != null) {
                    listener.onHandlerResponse(response,
                                               id.getSequenceCommand(),
                                               id.getActivity(),
                                               id.getConfiguration());
                } else {
                    LOG.info("No interested listener on action id " + actionId);
                }
                //now, remove the element from the queue
                _actionQueue.poll();
                //iterate to the next element
                id = _actionQueue.peek();
            }

        }
    }
}
