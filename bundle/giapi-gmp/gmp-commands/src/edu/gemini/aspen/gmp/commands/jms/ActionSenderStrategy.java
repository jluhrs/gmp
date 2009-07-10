package edu.gemini.aspen.gmp.commands.jms;

import edu.gemini.aspen.gmp.commands.Action;
import edu.gemini.aspen.gmp.commands.ActionSender;
import edu.gemini.aspen.gmp.commands.api.SequenceCommand;

/**
 * A simple utility class to get the action sender associated to a given Action
 */
public class ActionSenderStrategy {


    private ActionSender _default;
    private ActionSender _apply;

    public ActionSenderStrategy(JMSActionMessageProducer producer) {

        ActionMessageFactory factory = new ActionMessageFactory(producer);
        _default = new DefaultActionSender(factory);
        _apply   = new ApplyActionSender(factory);
    }


    public ActionSender getActionSender(Action action) {

        if (action.getSequenceCommand() == SequenceCommand.APPLY) return _apply;
        return _default;

    }

}
