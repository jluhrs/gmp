package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;

/**
 * The default sequence command executor. It will just send the
 * action to the instrument and will return the corresponding answer.
 */
public class DefaultSenderExecutor implements SequenceCommandExecutor {

    private ActionMessageBuilder _actionMessageBuilder;

    public DefaultSenderExecutor(ActionMessageBuilder builder) {
        _actionMessageBuilder = builder;
    }

    @Override
    public HandlerResponse execute(Action action, ActionSender sender) {
        ActionMessage m = _actionMessageBuilder.buildActionMessage(action);

        return sender.send(m, action.getTimeout());
    }

}
