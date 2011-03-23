package edu.gemini.aspen.gmp.commands.model.impl;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.executors.SequenceCommandExecutor;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

/**
 * Command Sender implementation.
 * This is the implementation used by OSGi clients to send commands
 * down to the client
 */
@Component
@Instantiate
@Provides
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
    public CommandSenderImpl(@Requires ActionManager manager,
                             @Requires ActionSender sender,
                             @Requires SequenceCommandExecutor executor) {
        Preconditions.checkArgument(manager != null, "ActionManager cannot be null");
        Preconditions.checkArgument(sender != null, "ActionSender cannot be null");
        Preconditions.checkArgument(executor != null, "SequenceCommandExecutor cannot be null");

        _manager = manager;
        _executor = executor;
        _sender = sender;
    }


    @Override
    public HandlerResponse sendCommand(Command command, CompletionListener listener) {
        return sendCommand(command, listener, Action.DEFAULT_COMMAND_RESPONSE_TIMEOUT);
    }

    @Override
    public HandlerResponse sendCommand(Command command, CompletionListener listener, long timeout) {
        CompletionListenerDecorator decoratorListener =
                new CompletionListenerDecorator(listener);

        Action action = new Action(command, decoratorListener, timeout);

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
