package edu.gemini.aspen.gmp.commands.model;

import java.util.Map;

/**
 * A message representing an Action. Implementers of
 * this interface can use different communication mechanism
 * to perform the actual communication.
 */
public interface ActionMessage {

    /**
     * Return the destination name associated to this action message
     * @return the destination name
     */
    String getDestinationName();

    /**
     * Get the message properties to be used in this message.
     * The Action ID and the activity of the action are encoded
     * as properties.
     * @return Map representing the properties of this message
     */
    Map<String, Object> getProperties();

    /**
     * The data elements for an action message are modeled as a
     * map. The data elements are the configuration associated
     * to the sequence command. If the sequence command does not
     * have a configuration associated, this method
     * returns <code>null</code>
     * @return Map with the configuration elements of the action
     */
    Map<String, Object> getDataElements();
}
