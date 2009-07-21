package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.commands.model.*;

/**
 * This is a high order Sequence Commnad Executor. It will delegate
 * the actual execution to a more specific executor.
 */
public class SequenceCommandExecutorStrategy implements SequenceCommandExecutor {


    private SequenceCommandExecutor _defaultExecutor = new DefaultSenderExecutor();
    private SequenceCommandExecutor _applyExecutor = new ApplySenderExecutor();
    private SequenceCommandExecutor _rebootExecutor = new RebootSenderExecutor();

    public HandlerResponse execute(Action action, ActionSender sender) {

        if (action == null)
            throw new SequenceCommandException("Null action received for execution");

        switch (action.getSequenceCommand()) {
            case APPLY:
                return _applyExecutor.execute(action, sender);
            case REBOOT:
                return _rebootExecutor.execute(action, sender);
            default:
                return _defaultExecutor.execute(action, sender);
        }

    }
}
