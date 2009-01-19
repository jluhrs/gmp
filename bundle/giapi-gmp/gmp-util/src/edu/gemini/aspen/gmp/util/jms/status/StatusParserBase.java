package edu.gemini.aspen.gmp.util.jms.status;

import edu.gemini.aspen.gmp.status.api.StatusItem;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

/**
 * Base class for all the Status Parsers. Reads the
 * name and the value of the status item from the BytesMessage.
 */
public abstract class StatusParserBase<T> implements StatusParser {
    /**
     * All the Status items have name and a certain value.
     * This method deals with that, delegating the read of the particular
     * value to the implementing subclasses, in addition to
     * the constructing the specific type of the status item needed
     *
     * @param bm The BytesMessage to be parsed
     * @return Status Item contained in the byte message
     * @throws JMSException in case of problems reading the message
     */
    public final StatusItem parse(BytesMessage bm) throws JMSException {
        String name = bm.readUTF();
        T value = getValue(bm);
        return buildStatusItem(name, value, bm);
    }

    /**
     * Get the particular value for a status item out of the JMS message
     * @param bm the JMS message containing the value
     * @return value contained in the message
     * @throws JMSException in case of problems reading the message
     */
    abstract T getValue(BytesMessage bm) throws JMSException;

    /**
     * Build a specific status item object, given the specified name
     * and value. The JMS message is used to extract any additional
     * information needed to construct the specific Status Item.
     * Implementing classes will decide how to parse the message, if
     * needed
     * @param name name of the status item to construct
     * @param value the value of the status item
     * @param bm JMS message containing additional information for
     * the status item
     * @return an instance of a Status Item
     * @throws JMSException in case there are problems reading the
     * information from the JMS message
     */
    abstract StatusItem buildStatusItem(String name, T value, BytesMessage bm) throws JMSException;
}
