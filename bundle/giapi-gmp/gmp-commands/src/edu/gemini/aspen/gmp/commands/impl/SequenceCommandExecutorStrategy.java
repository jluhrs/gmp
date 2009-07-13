package edu.gemini.aspen.gmp.commands.impl;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.commands.Action;
import edu.gemini.aspen.gmp.commands.ActionSender;
import edu.gemini.aspen.gmp.commands.SequenceCommandExecutor;
import edu.gemini.aspen.gmp.commands.SequenceCommandException;

/**
 * This is a high order Sequence Commnad Executor. It will delegate
 * the actual execution to a more specific executor.
 */
public class SequenceCommandExecutorStrategy implements SequenceCommandExecutor{


    private SequenceCommandExecutor defaultExecutor = new DefaultSenderCommand();
    private SequenceCommandExecutor applyExecutor = new ApplySenderCommand();

    public HandlerResponse execute(Action action, ActionSender sender) {

        if (action == null)
            throw new SequenceCommandException("Null action received for execution");

        switch (action.getSequenceCommand()) {
            case APPLY:
                return applyExecutor.execute(action, sender);
            default:
                return defaultExecutor.execute(action, sender);
        }
        
    }
}
