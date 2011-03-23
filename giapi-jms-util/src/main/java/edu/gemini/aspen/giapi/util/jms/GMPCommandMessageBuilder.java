package edu.gemini.aspen.giapi.util.jms;

import edu.gemini.jms.api.MapMessageBuilder;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

/**
 * This class can construct a JMS message representing a command in the protocol between a client
 * and the CommandSender.
 */
public class GMPCommandMessageBuilder implements MapMessageBuilder {
    private final String correlationID;
    private final Map<String, String> messageBody;
    private final Map<String, String> messageProperties;

    public GMPCommandMessageBuilder(String correlationID, Map<String, String> messageBody, Map<String, String> messageProperties) {
        this.correlationID = correlationID;
        this.messageBody = messageBody;
        this.messageProperties = messageProperties;
    }

    @Override
    public MapMessage constructMessageBody(MapMessage message) throws JMSException {
        message.setJMSCorrelationID(correlationID);
        for (Map.Entry<String, String> property : messageBody.entrySet()) {
            message.setString(property.getKey(), property.getValue());
        }
        for (Map.Entry<String, String> property : messageProperties.entrySet()) {
            message.setStringProperty(property.getKey(), property.getValue());
        }
        return message;
    }
}
