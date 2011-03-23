package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;

import com.google.common.base.Preconditions;
import edu.gemini.jms.api.AbstractMapMessageBuilder;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

/**
 * This class is able to create a message representing a command to be sent
 * to the instrument. The GIAPI-GLUE is able to deconstruct this message
 * and direct its execution
 */
class InstrumentCommandMessageBuilder extends AbstractMapMessageBuilder {
    private final Map<String, Object> messageBody;
    private final Map<String, Object> properties;

    public InstrumentCommandMessageBuilder(Map<String, Object> messageBody, Map<String, Object> properties) {
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
