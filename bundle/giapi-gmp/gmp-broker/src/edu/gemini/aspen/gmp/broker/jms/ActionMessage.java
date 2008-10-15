package edu.gemini.aspen.gmp.broker.jms;

import edu.gemini.aspen.gmp.commands.api.Configuration;

import javax.jms.MapMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Enumeration;

/**
 * A message containing an Action using JMS as the
 * underlying communication mechanism mechanism.
 */
public class ActionMessage {

    private MapMessage _message;

    /**
     * Constructor. Creates an Action message using a MapMessage
     * @param msg the MapMessage used to backup this action
     */
    public ActionMessage(MapMessage msg) {
        _message = msg;
    }

    /**
     * Set the configuration to be sent using this message
     * @param config The Configuration to be stored in this message
     * @throws JMSException if there is a problem storing the
     *         configuration in the JMS message
     */
    public void setConfiguration(Configuration config) throws JMSException {
        if (config != null) {
            //set all the configuration elements in the map
            Enumeration<String> e = config.getKeys();
            while (e.hasMoreElements()) {
                String key = e.nextElement();
                String value = config.getValue(key);
                _message.setString(key, value);
            }
        }
    }

    /**
     * Returns the JMS message used to back up this message
     * @return the JMS message used to back up this message
     */
    public Message getJmsMessage() {
        return _message;
    }

}
