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
public class ActionManager {

    private static final Logger LOG = Logger.getLogger(
            ActionManager.class.getName());

    private final Queue<Action> _actionQueue = new ConcurrentLinkedQueue<Action>();


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
     * @return The Action associated to this sequence command.
     */
    public Action registerCommand(SequenceCommand command,
                                    Activity activity,
                                    Configuration config,
                                    CompletionListener listener) {
        Action action = new Action(command, activity, config, listener);
        _actionQueue.add(action);
        LOG.info(
                "Registering new command " + command + " with action id " + action
                        .getId());
        return action;
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
        synchronized (_actionQueue) {
            Action action = _actionQueue.peek();

            if (action == null) {
                LOG.info(
                        "I don't know about action ID " + actionId + ". Usually this means a problem in the instrument code.");
                return;
            }
            //If the first action in the queue is bigger
            //than the one received, we don't have anything to do. Log this
            //since it's an indication something weird is happening
            if (action.getId() > actionId) {
                LOG.log(Level.SEVERE,
                        "Action ID received " + actionId + " but we don't have issued that action yet");
                return;
            }

            while (action != null && action.getId() <= actionId) {
                LOG.info("Notifying listeners for action ID " + action);
                CompletionListener listener = action.getCompletionListener();
                if (listener != null) {
                    listener.onHandlerResponse(response,
                                               action.getSequenceCommand(),
                                               action.getActivity(),
                                               action.getConfiguration());
                } else {
                    LOG.info("No interested listener on action action " + actionId);
                }
                //now, remove the element from the queue
                _actionQueue.poll();
                //iterate to the next element
                action = _actionQueue.peek();
            }

        }
    }
}
