package edu.gemini.aspen.gmp.broker.commands;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;

/**
 *  An Action Sender is in charge of sending a given action over the network
 */
public interface ActionSender {

    /**
     * This method takes the given action and converts it into a message to
     * be dispatched over the network
     *
     * @param action The action to be sent via the network
     *
     * @return HandlerResponse associated to the given action. If no response is
     *         received, an ERROR HandlerResponse is returned
     */
    HandlerResponse send(Action action);
    
}
