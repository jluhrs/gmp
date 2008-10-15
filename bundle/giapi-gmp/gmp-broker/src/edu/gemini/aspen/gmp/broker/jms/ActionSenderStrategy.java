package edu.gemini.aspen.gmp.broker.jms;

import edu.gemini.aspen.gmp.broker.commands.Action;
import edu.gemini.aspen.gmp.broker.commands.ActionSender;
import edu.gemini.aspen.gmp.commands.api.SequenceCommand;

/**
 * A simple utility class to get the action sender associated to a given Action
 */
public class ActionSenderStrategy {

    private final static ActionSender DEFAULT = new DefaultActionSender();
    private final static ActionSender APPLY   = new ApplyActionSender();


    public static ActionSender getActionSender(Action action) {

        if (action.getSequenceCommand() == SequenceCommand.APPLY) return APPLY;

        return DEFAULT;

    }

}
