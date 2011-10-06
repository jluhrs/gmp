package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.RebootArgument;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.RebootManager;

/**
 * An executor for the REBOOT sequence command.It will initiate the reboot
 * if the activity is PRESET or PRESET/START and will return immediately
 * to the client with a COMPLETED response.
 * <br>
 * It is an error to try to CANCEL a REBOOT sequence command. The
 * reboot itself is delegated to a {@link RebootManager} object that
 * will process the command. The arguments are validated by this class
 * before passing them to the {@link RebootManager}.
 */
public class RebootSenderExecutor implements SequenceCommandExecutor {
    private final RebootManager _rebootManager;

    public RebootSenderExecutor(RebootManager rebootManager) {
        _rebootManager = rebootManager;
    }

    @Override
    public HandlerResponse execute(Action action, ActionSender sender) {

        final RebootArgument arg = RebootArgument.parse(action.getCommand().getConfiguration());
        if (arg == null) {
            return HandlerResponse.createError("Invalid argument for the REBOOT sequence command: " + action.getCommand().getConfiguration());
        }

        Activity rebootActivity = action.getCommand().getActivity();
        if (rebootActivity == Activity.PRESET) {
            return HandlerResponse.ACCEPTED;
        }
        if (rebootActivity == Activity.CANCEL) {
            return HandlerResponse.createError("Can't cancel a REBOOT sequence command");
        }

        //We have a START or PRESET_START. Let's initiate the reboot in a different thread
        new Thread() {
            @Override
            public void run() {
                _rebootManager.reboot(arg);
            }
        }.start();

        //and always return COMPLETED.
        return HandlerResponse.COMPLETED;
    }
}
