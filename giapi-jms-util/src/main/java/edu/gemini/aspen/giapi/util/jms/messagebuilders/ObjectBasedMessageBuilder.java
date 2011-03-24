package edu.gemini.aspen.giapi.util.jms.messagebuilders;

import com.google.common.base.Preconditions;
import edu.gemini.jms.api.AbstractMapMessageBuilder;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

/**
 * This class is able to create a message containing a given set of body properties
 * and strings
 * It can be used, for example to build message to communicate between GMP-commands and an
 * instrument.
 */
public class ObjectBasedMessageBuilder extends AbstractMapMessageBuilder {
    private final Map<String, Object> messageBody;
    private final Map<String, Object> properties;

    public ObjectBasedMessageBuilder(Map<String, Object> messageBody, Map<String, Object> properties) {
        super();
        Preconditions.checkArgument(messageBody != null);
        Preconditions.checkArgument(properties != null);
        
        this.messageBody = messageBody;
        this.properties = properties;
    }

    @Override
    public MapMessage constructMessageBody(MapMessage message) throws JMSException {
        super.setMessageBody(message, messageBody);
        super.setMessageProperties(message, properties);
        
        return message;
    }
}
