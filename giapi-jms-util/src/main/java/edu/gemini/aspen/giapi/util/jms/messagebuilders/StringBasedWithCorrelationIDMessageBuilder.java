package edu.gemini.aspen.giapi.util.jms.messagebuilders;

import com.google.common.base.Preconditions;
import edu.gemini.jms.api.MapMessageBuilder;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

/**
 * This class can construct a JMS message using just string and it contains also a
 * a correlationID
 *
 * For example this can build message used for JMS-based command interaction
 */
public class StringBasedWithCorrelationIDMessageBuilder implements MapMessageBuilder {
    private final String correlationID;
    private final Map<String, String> messageBody;
    private final Map<String, String> messageProperties;

    public StringBasedWithCorrelationIDMessageBuilder(String correlationID, Map<String, String> messageBody, Map<String, String> messageProperties) {
        Preconditions.checkArgument(correlationID != null, "CorrelationID cannot be null");
        Preconditions.checkArgument(!correlationID.isEmpty(), "CorrelationID cannot be empty");
        Preconditions.checkArgument(messageBody != null, "Message body components cannot be null");
        Preconditions.checkArgument(messageProperties != null, "Message properties cannot be null");
        
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
