package edu.gemini.aspen.giapi.status.setter;

import edu.gemini.aspen.giapi.status.StatusItem;

import javax.jms.JMSException;

/**
 * Interface StatusSetter
 *
 */
public interface StatusSetter {
    /**
     * Sets the value of an status item
     * @param statusItem
     */
    boolean setStatusItem(StatusItem statusItem) throws JMSException;
}
