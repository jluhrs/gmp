package edu.gemini.aspen.gmp.broker.jms;

import edu.gemini.aspen.gmp.broker.commands.Action;
import edu.gemini.aspen.gmp.broker.commands.ActionMessage;
import edu.gemini.aspen.gmp.commands.api.ConfigPath;

/**
 * Factory to create Action Messages.
 */
public class ActionMessageFactory {

    JMSActionMessageProducer _producer;

    public ActionMessageFactory(JMSActionMessageProducer producer) {
        _producer = producer;
    }

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
    public ActionMessage create(Action action) {
        return create(action, null, MessagingSystem.JMS);
    }

    /**
     * Create an action message factory considering the action
     * and the given config path. This method is used when the
     * path needs to be considered when sending the message,
     * as it occurs with the Apply sequence command.
     * @param action the action to be sent
     * @param path path to be used to figure out the destination
     * of this message
     * @return a new Action Message using the default MessagingSystem
     */
    public ActionMessage create(Action action, ConfigPath path) {
        return create(action, path, MessagingSystem.JMS);
    }

    /**
     * Create a new message to represent an action
     * using the specified messaging type.
     * @param action The action to be sent
     * @param path The path used to identify this action message (valid only for the Apply sequence Command)
     * @param type Messaging system type to be used as
     *        the underlying communication system
     * @return a new Action Message for this action. 
     */
    public ActionMessage create(Action action,
                                       ConfigPath path,
                                       MessagingSystem type) {
        switch (type) {
            case JMS:
                return new JmsActionMessage(_producer, action, path);
            default:
                return new JmsActionMessage(_producer, action, path);
        }
    }

}
