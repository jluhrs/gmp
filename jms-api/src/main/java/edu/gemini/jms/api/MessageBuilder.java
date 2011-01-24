package edu.gemini.jms.api;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.JMSException;
import java.util.Map;

/**
 * Utility class to construct Messages out of different data structures. 
 */
public class MessageBuilder {

    public void buildMapMessage(MapMessage mm, Map<String, Object> message) {
         try {

            for (String key : message.keySet()) {
                mm.setObject(key, message.get(key));
            }
        } catch (JMSException e) {
            throw new MessagingException("Unable to build message from " + message,
                    e);

        }
    }

    public void setMessageProperties(Message m, Map<String, Object> properties) {
        try {

            for (String key : properties.keySet()) {
                m.setObjectProperty(key, properties.get(key));
            }
        } catch (JMSException e) {
            throw new MessagingException("Unable to set properties from " + properties,
                    e);
        }

    }
}
