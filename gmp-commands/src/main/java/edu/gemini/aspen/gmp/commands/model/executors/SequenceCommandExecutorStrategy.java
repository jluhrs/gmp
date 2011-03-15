package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.IActionManager;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandExecutor;
import edu.gemini.aspen.gmp.commands.model.reboot.LogRebootManager;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

/**
 * This is a high order Sequence Command Executor. It will delegate
 * the actual execution to a more specific executor.
 */
@Component
@Instantiate
@Provides
public class SequenceCommandExecutorStrategy implements SequenceCommandExecutor {
    private final SequenceCommandExecutor _defaultExecutor;
    private final SequenceCommandExecutor _applyExecutor;
    private final SequenceCommandExecutor _rebootExecutor;

    /**
     * Construct the executor specifying the ActionMessageBuilder to use.
     * @param builder ActionMessageBuilder to be used.
     * @param manager the Action Manager that keeps track of the actions
     */
    public SequenceCommandExecutorStrategy(@Requires ActionMessageBuilder builder,
                                           @Requires IActionManager manager) {
        _defaultExecutor = new DefaultSenderExecutor(builder);
        _applyExecutor = new ApplySenderExecutor(builder, manager);
        _rebootExecutor = new RebootSenderExecutor(new LogRebootManager()
        );
    }

    @Override
    public HandlerResponse execute(Action action, ActionSender sender) {
        if (action == null)
            throw new SequenceCommandException("Null action received for execution");

        Command command = action.getCommand();
        return findCommandExecutor(command).execute(action, sender);
    }

    private SequenceCommandExecutor findCommandExecutor(Command sequenceCommand) {
        switch (sequenceCommand.getSequenceCommand()) {
            case APPLY:
                return _applyExecutor;
            case REBOOT:
                return _rebootExecutor;
            default:
                return _defaultExecutor;
        }
    }
}
