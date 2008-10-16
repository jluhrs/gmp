package edu.gemini.aspen.gmp.broker.jms;

import edu.gemini.aspen.gmp.broker.commands.Action;
import edu.gemini.aspen.gmp.broker.commands.ActionMessage;

/**
 * Factory to create Action Messages.
 */
public class ActionMessageFactory {

    public enum MessagingSystem {
        JMS
    }

    /**
     * Default action message factory. Use the default
     * messaging system to instantiate a new action message
     * @param action the action to be sent
     * @return a new ActionMessage using the default
     *         messaging system
     */
    public static ActionMessage create(Action action) {
        return create(action, MessagingSystem.JMS);
    }

    /**
     * Create a new message to represent an action
     * using the specified messaging type.
     * @param action The action to be sent
     * @param type Messaging system type to be used as
     *        the underlying communication system
     * @return a new Action Message for this action. 
     */
    public static ActionMessage create(Action action, MessagingSystem type) {
        switch (type) {
            case JMS:
                return new JmsActionMessage(action);
            default:
                return new JmsActionMessage(action);
        }
    }

}
