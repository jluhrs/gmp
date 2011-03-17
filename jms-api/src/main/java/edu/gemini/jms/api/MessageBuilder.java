package edu.gemini.jms.api;

import com.google.common.base.Preconditions;

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

    public void fillStringBasedMapMessage(MapMessage mm, Map<String, String> message) {
        try {

            for (String key : message.keySet()) {
                mm.setObject(key, message.get(key));
            }
        } catch (JMSException e) {
            throw new MessagingException("Unable to build message from " + message,
                    e);

        }
    }

    public void setStringMessageProperties(Message m, Map<String, String> properties) {
        Preconditions.checkArgument(m != null);
        Preconditions.checkArgument(properties != null);

        try {
            for (String key : properties.keySet()) {
                m.setStringProperty(key, properties.get(key));
            }
        } catch (JMSException e) {
            throw new MessagingException("Unable to set properties from " + properties,
                    e);
        }

    }
}
