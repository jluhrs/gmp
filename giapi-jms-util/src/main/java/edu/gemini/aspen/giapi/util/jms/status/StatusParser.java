package edu.gemini.aspen.giapi.util.jms.status;

import edu.gemini.aspen.giapi.status.StatusItem;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

/**
 * A Status parser is capable of constructing a Status Item out
 * of a JMS Bytes Message
 */
public interface StatusParser<T> {
    /**
     * Construct a Status Item object from the content of the
     * given BytesMessage JMS message
     * @param bm The BytesMessage to be parsed
     * @return the new StatusItem contained in the message or
     * <code>null</code> if there is none.  
     * @throws JMSException if there is a problem accessing
     * the JMS message information
     */
    StatusItem<T> parse(BytesMessage bm) throws JMSException;
}
