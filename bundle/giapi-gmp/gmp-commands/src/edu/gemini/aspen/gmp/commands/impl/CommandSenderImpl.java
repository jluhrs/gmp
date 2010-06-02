package edu.gemini.aspen.gmp.commands.impl;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionManager;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandExecutor;

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
     *
     * @param manager  The action manager that holds information
     *                 about the actions that are being executed
     * @param sender   sender to use to dispatch actions with the
     *                 given executor
     * @param executor the executor that will be in charge
     *                 of processing the actions using the given sender.
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
     * @return a HandlerResponse, used to decide if the command was accepted by
     *         the client.
     */
    public HandlerResponse sendSequenceCommand(SequenceCommand command,
                                               Activity activity,
                                               CompletionListener listener) {
        return sendSequenceCommand(command, activity, null, listener);

    }


    /**
     * A decorator for the Completion Listener that will be used. This
     * provides a mechanism to recover the response provided
     * for and action, in case that answer arrives _before_ we
     * provide an answer to the system
     */
    public class CompletionListenerDecorator implements CompletionListener {

        private CompletionListener _listener;
        private HandlerResponse _response;

        public CompletionListenerDecorator(CompletionListener l) {
            _listener = l;
        }

        HandlerResponse getResponse() {
            return _response;
        }

        public void onHandlerResponse(HandlerResponse response, SequenceCommand command, Activity activity, Configuration config) {
            _listener.onHandlerResponse(response, command, activity, config);
            //register the response received. 
            _response = response;
        }
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
     * @return a HandlerResponse, used to decide if the command was accepted by
     *         the client.
     */
    public HandlerResponse sendSequenceCommand(SequenceCommand command,
                                               Activity activity,
                                               Configuration config,
                                               CompletionListener listener) {

        CompletionListenerDecorator decoratorListener =
                new CompletionListenerDecorator(listener);

        Action action = new Action(command, activity, config,
                decoratorListener);

        //first, register the action even if it's something that
        //will complete immediately.
        _manager.registerAction(action);

        HandlerResponse response = _executor.execute(action, _sender);

        //The only response that indicates actions have started is
        //STARTED. The other three do not result in any ongoing actions
        //that must be completed at a later time, therefore the
        //Completion listener is ignored in this case. See GIAPI design
        //and use, section 10.6
        if (response != null) {
            if (response.getResponse() == HandlerResponse.Response.STARTED) {
                //now, it is possible the action has completed _while_ we were
                //here.... let's take care of that case and if so, use the
                //answer from the listener to cover that case

                //acquires a lock on the manager, so
                //we ensure we are not
                //processing handlers while we validate the
                //action has finished
                _manager.lock();  
                try {
                    if (decoratorListener.getResponse() != null) {
                        response = decoratorListener.getResponse();
                        //this action is no longer valid.
                        _manager.unregisterAction(action);
                    }
                } finally {
                    _manager.unlock(); //release the lock on the manager.
                }
            } else {
                //since we don't expect the action to complete later, we
                //will remove it from the ones currently being monitored.
                _manager.unregisterAction(action);
            }
        }
        return response;
    }
}
