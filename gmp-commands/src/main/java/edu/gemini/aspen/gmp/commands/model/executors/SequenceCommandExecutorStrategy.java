package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import edu.gemini.aspen.gmp.commands.model.impl.ActionManager;
import edu.gemini.aspen.gmp.commands.model.reboot.LinuxRebootManager;
import edu.gemini.aspen.gmp.commands.model.reboot.LogRebootManager;
import org.apache.felix.ipojo.annotations.*;

/**
 * This is a high order Sequence Command Executor. It will delegate
 * the actual execution to a more specific executor.
 * <p/>
 * As this class is exported as an OSGi service it will be used by ActionManager
 */
@Component
@Instantiate
@Provides
public class SequenceCommandExecutorStrategy implements SequenceCommandExecutor {
    private final SequenceCommandExecutor _defaultExecutor;
    private final SequenceCommandExecutor _applyExecutor;
    private SequenceCommandExecutor _rebootExecutor;

    /**
     * Construct the executor specifying the ActionMessageBuilder to use.
     *
     * @param builder ActionMessageBuilder to be used.
     * @param manager the Action Manager that keeps track of the actions
     */
    public SequenceCommandExecutorStrategy(@Requires ActionMessageBuilder builder,
                                           @Requires ActionManager manager) {
        _defaultExecutor = new DefaultSenderExecutor(builder);
        _applyExecutor = new ApplySenderExecutor(builder, manager);
        _rebootExecutor = new RebootSenderExecutor(new LogRebootManager());
    }

    @Bind(optional = true)
    public void bindCommandSender(CommandSender sender) {
        _rebootExecutor = new RebootSenderExecutor(new LinuxRebootManager(sender));
    }

    @Unbind
    public void unbindCommandSender(CommandSender sender) {
        _rebootExecutor = new RebootSenderExecutor(new LogRebootManager());
    }

    @Override
    public HandlerResponse execute(Action action, ActionSender sender) {
        if (action == null) {
            throw new SequenceCommandException("Null action received for execution");
        }

        Command command = action.getCommand();
        return findCommandExecutor(command.getSequenceCommand()).execute(action, sender);
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
