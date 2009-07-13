package edu.gemini.aspen.gmp.commands.impl;

import edu.gemini.aspen.gmp.commands.api.CommandSender;
import edu.gemini.aspen.gmp.commands.ActionManager;
import edu.gemini.aspen.gmp.commands.Action;
import edu.gemini.aspen.gmp.commands.api.*;
import edu.gemini.aspen.gmp.commands.ActionSender;
import edu.gemini.aspen.gmp.commands.SequenceCommandExecutor;

/**
 * Command Sender implementation
 */
public class CommandSenderImpl implements CommandSender {

    /**
     * Holds state of the actions being tracked in the system
     */
    private final ActionManager _manager;

    /**
     * Process the actions received by the system using
     * the also provided ActionSender
     */
    private final SequenceCommandExecutor _executor;

    /**
     * Defines the mechanism to dispatch actions over the
     * available communication mechanism. 
     */
    private final ActionSender _sender;

    /**
     * Constructor
     * @param manager The action manager that holds information
     * about the actions that are being executed
     * @param sender sender to use to dispatch actions with the
     * given executor
     * @param executor the executor that will be in charge
     * of processing the actions using the given sender.
     */
    public CommandSenderImpl(ActionManager manager,
                             ActionSender sender,
                             SequenceCommandExecutor executor) {
        _manager = manager;
        _executor = executor;
        _sender = sender;
    }


    /**
     * Send a SequenceCommand with the specified activity to the registered
     * clients.
     * <p/>
     * Synchronously wait for the recipient to notify the command was received
     * and returns a HandlerResponse back to the caller.
     * <p/>
     * If there is no answer for a defined period of time, the call will return
     * a HandlerResponse containing an error message.
     *
     * @param command  The Sequence command to send, like INIT or REBOOT
     * @param activity The associated activities to be executed for the
     *                 specified sequence command, like PRESET or START
     * @param listener Completion listener that will be invoked if the
     *                 HandlerResponse is STARTED. The listener will be invoked
     *                 whenever the completion information for this on-going
     *                 action is available. Otherwise this listener is ignored.
     *
     * @return a HandlerResponse, used to decide if the command was accepted by
     *         the client.
     */
    public HandlerResponse sendSequenceCommand(SequenceCommand command,
                                               Activity activity,
                                               CompletionListener listener) {
        return sendSequenceCommand(command, activity, null, listener);

    }

    /**
     * Send a SequenceCommand with the specified activity and configuration to
     * the registered clients.
     * <p/>
     * Synchronously wait for the recipient to notify the command was received
     * and returns a HandlerResponse back to the caller.
     * <p/>
     * If there is no answer for a defined period of time, the call will return
     * a HandlerResponse containing an error message.
     *
     * @param command  The Sequence command to send, like INIT or REBOOT
     * @param activity The associated activities to be executed for the
     *                 specified sequence command, like PRESET or START
     * @param config   the configuration that will be send along with the
     *                 sequence command
     * @param listener Completion listener that will be invoked if the
     *                 HandlerResponse is STARTED. The listener will be invoked
     *                 whenever the completion information for this on-going
     *                 action is available. Otherwise this listener is ignored.
     *
     * @return a HandlerResponse, used to decide if the command was accepted by
     *         the client.
     */
    public HandlerResponse sendSequenceCommand(SequenceCommand command,
                                               Activity activity,
                                               Configuration config,
                                               CompletionListener listener) {
        Action action = new Action(command, activity, config,
                                           listener);

        HandlerResponse response = _executor.execute(action, _sender);

        //The only response that indicates actions have started is
        //STARTED. The other three do not result in any ongoing actions
        //that must be completed at a later time, therefore the
        //Completion listener is ignored in this case. See GIAPI design
        //and use, section 10.6
        if (response != null &&
                response.getResponse() == HandlerResponse.Response.STARTED) {
            //register this action as one that we need to provide completion
            //information later.
            _manager.registerAction(action);
        } 
        return response;
    }
}
