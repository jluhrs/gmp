package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.commands.model.*;
import edu.gemini.aspen.gmp.commands.model.reboot.LogRebootManager;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;

/**
 * This is a high order Sequence Commnad Executor. It will delegate
 * the actual execution to a more specific executor.
 */
public class SequenceCommandExecutorStrategy implements SequenceCommandExecutor {


    private SequenceCommandExecutor _defaultExecutor;
    private SequenceCommandExecutor _applyExecutor;
    private SequenceCommandExecutor _rebootExecutor;

    /**
     * Construct the executor specifying the ActionMessageBuilder to use.
     * @param builder ActionMessageBuilder to be used.
     * @param manager the Action Manager that keeps track of the actions
     */
    public SequenceCommandExecutorStrategy(ActionMessageBuilder builder,
                                           ActionManager manager) {

        _defaultExecutor = new DefaultSenderExecutor(builder);
        _applyExecutor = new ApplySenderExecutor(builder, manager);
        _rebootExecutor = new RebootSenderExecutor(
                builder,
                new LogRebootManager()
        );
    }

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
