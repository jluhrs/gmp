package edu.gemini.aspen.gmp.commands;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;

/**
 *  An Action Sender is in charge of sending a given action message over
 * the network
 */
public interface ActionSender {

    /**
     * This method takes the given action and converts it into a message to
     * be dispatched over the network
     *
     * @param message The action message to be sent via the network
     *
     * @return HandlerResponse associated to the given action. If no response is
     *         received, a NO_ANSWER HandlerResponse is returned
     */
    HandlerResponse send(ActionMessage message);
    
}
