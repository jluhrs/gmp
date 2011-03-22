package edu.gemini.jms.api;

import com.google.common.base.Preconditions;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.Map;

/**
 * Utility class to construct Messages out of different data structures.
 */
public abstract class AbstractMapMessageBuilder implements MapMessageBuilder {

    public void setMessageBody(MapMessage mm, Map<String, Object> messageBody) {
        Preconditions.checkArgument(mm != null);
        Preconditions.checkArgument(messageBody != null);
        try {
            for (String key : messageBody.keySet()) {
                mm.setObject(key, messageBody.get(key));
            }
        } catch (JMSException e) {
            throw new MessagingException("Unable to build messageBody from " + messageBody,
                    e);
        }
    }

    public void setMessageProperties(Message m, Map<String, Object> properties) {
        Preconditions.checkArgument(m != null);
        Preconditions.checkArgument(properties != null);
        try {
            for (String key : properties.keySet()) {
                m.setObjectProperty(key, properties.get(key));
            }
        } catch (JMSException e) {
            throw new MessagingException("Unable to set properties from " + properties,
                    e);
        }
    }

    public void setStringBasedMessageBody(MapMessage mm, Map<String, String> message) {
        Preconditions.checkArgument(mm != null);
        Preconditions.checkArgument(message != null);

        try {
            for (String key : message.keySet()) {
                mm.setString(key, message.get(key));
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
