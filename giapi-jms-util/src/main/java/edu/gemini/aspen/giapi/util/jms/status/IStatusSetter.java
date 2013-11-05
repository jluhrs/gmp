package edu.gemini.aspen.giapi.util.jms.status;

import edu.gemini.aspen.giapi.status.StatusItem;

import javax.jms.JMSException;

/**
 * Interface IStatusSetter
 *
 * @author Nicolas A. Barriga
 *         Date: 7/6/12
 */
public interface IStatusSetter {
    public boolean setStatusItem(StatusItem statusItem) throws JMSException;
}
