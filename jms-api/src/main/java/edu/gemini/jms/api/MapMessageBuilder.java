package edu.gemini.jms.api;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * This interface defines objects that can convert a regular message into one ready to be sent
 * <br>
 * For example an implementation could set parameters and members of the message
 */
public interface MapMessageBuilder {
    /**
     * This method is called with a MapMessage before being sent over JMS
     * An implementation can modify the message, for example setting the properties
     * or the body of the message
     * 
     * @param message A valid message to be filled by the builder
     * @return The message ready to be sent
     * @throws JMSException
     */
    MapMessage constructMessageBody(MapMessage message) throws JMSException;
}
