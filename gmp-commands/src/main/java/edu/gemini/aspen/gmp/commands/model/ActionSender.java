package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.giapi.commands.HandlerResponse;

/**
 *  An Action Sender is in charge of sending a given action message over
 * the network
 */
public interface ActionSender {
    int RESPONSE_WAIT_TIMEOUT = 500;

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

    /**
     * This method takes the given action and converts it into a message to
     * be dispatched over the network
     *
     * @param message The action message to be sent via the network
     * @param timeout Maximum time to wait for an answer before responding NO_ANSWER
     *
     * @return HandlerResponse associated to the given action. If no response is
     *         received, a NO_ANSWER HandlerResponse is returned
     */
    HandlerResponse send(ActionMessage message, long timeout);
    
}
