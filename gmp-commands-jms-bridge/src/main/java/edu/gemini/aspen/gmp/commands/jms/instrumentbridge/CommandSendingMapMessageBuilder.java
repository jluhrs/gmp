package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;

import edu.gemini.jms.api.AbstractMapMessageBuilder;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

/**
 * This class is able to create a message filling its Object properties
 * and its body properties out of the
 */
class CommandSendingMapMessageBuilder extends AbstractMapMessageBuilder {
    private final Map<String, Object> messageBody;
    private final Map<String, Object> properties;

    public CommandSendingMapMessageBuilder(Map<String, Object> messageBody, Map<String, Object> properties) {
        super();
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
