package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.gmp.commands.model.*;
import edu.gemini.aspen.gmp.commands.model.reboot.LogRebootManager;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;

/**
 * This is a high order Sequence Command Executor. It will delegate
 * the actual execution to a more specific executor.
 */
public class SequenceCommandExecutorStrategy implements SequenceCommandExecutor {
    private final SequenceCommandExecutor _defaultExecutor;
    private final SequenceCommandExecutor _applyExecutor;
    private final SequenceCommandExecutor _rebootExecutor;

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

    @Override
    public HandlerResponse execute(Action action, ActionSender sender) {
        if (action == null)
            throw new SequenceCommandException("Null action received for execution");

        return findCommandExecutor(action.getSequenceCommand()).execute(action, sender);
    }

    private SequenceCommandExecutor findCommandExecutor(SequenceCommand sequenceCommand) {
        switch (sequenceCommand) {
            case APPLY:
                return _applyExecutor;
            case REBOOT:
                return _rebootExecutor;
            default:
                return _defaultExecutor;
        }
    }
}
