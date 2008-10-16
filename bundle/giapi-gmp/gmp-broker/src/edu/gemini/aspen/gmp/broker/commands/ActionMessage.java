package edu.gemini.aspen.gmp.broker.commands;

import edu.gemini.aspen.gmp.commands.api.Configuration;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;

/**
 * A message representation of an Action. Implementers of
 * this interface can use different communication mechanism
 * to perform the actual communication.
 */
public interface ActionMessage {

    /**
     * Set configuration information in the message
     * @param config The configuration information to be set
     */
    void setConfiguration(Configuration config);

    /**
     * Send this action using the underlying communication
     * mechanisms. This method returns the acknowledge
     * from the client that the message was received.
     * The acknowledge is received synchronously by this
     * call.
     *  
     * @return A HandlerResponse containing the acknowledge
     * information for this message.
     */
    HandlerResponse send();
}
