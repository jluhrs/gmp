package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.giapi.commands.ConfigPath;

/**
 * Interface defining a builder of action messages
 *
 */
public interface ActionMessageBuilder {
    /**
     * Builds an ActionMessage for the specified action
     *
     * @param action the action to be used to construct the message
     * @return a new ActionMessage
     */
    ActionMessage buildActionMessage(Action action);

    /**
     * Builds an action message for the specified action, but
     * only containing the configuration information that
     * matches the provided ConfigPath
     *
     * @param action the action to be used to construct the message
     * @param path   used to filter the configuration. Only the configuration
     *               that matches the given path will be encoded in the message
     * @return a new ActionMessage containing the sub-configuration defined
     *         by the ConfigPath
     */
    ActionMessage buildActionMessage(Action action, ConfigPath path);
}
