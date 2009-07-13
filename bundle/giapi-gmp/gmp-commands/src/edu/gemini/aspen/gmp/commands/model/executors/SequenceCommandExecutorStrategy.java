package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandExecutor;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;

/**
 * This is a high order Sequence Commnad Executor. It will delegate
 * the actual execution to a more specific executor.
 */
public class SequenceCommandExecutorStrategy implements SequenceCommandExecutor{


    private SequenceCommandExecutor defaultExecutor = new DefaultSenderExecutor();
    private SequenceCommandExecutor applyExecutor = new ApplySenderExecutor();

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
