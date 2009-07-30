package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandExecutor;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.messaging.ActionMessageBuilder;

/**
 * The default sequence command executor. It will just send the
 * action to the instrument and will return the corresponding answer.
 */
public class DefaultSenderExecutor implements SequenceCommandExecutor {

    ActionMessageBuilder _actionMessageBuilder;

    public DefaultSenderExecutor(ActionMessageBuilder builder) {
        _actionMessageBuilder = builder;
    }

    public HandlerResponse execute(Action action, ActionSender sender) {

        ActionMessage m = _actionMessageBuilder.buildActionMessage(action);

        return sender.send(m);

    }
    


}
