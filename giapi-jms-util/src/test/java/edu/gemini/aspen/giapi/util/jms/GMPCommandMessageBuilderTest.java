package edu.gemini.aspen.giapi.util.jms;

import com.google.common.collect.ImmutableMap;
import edu.gemini.aspen.giapi.util.jms.test.MapMessageMock;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GMPCommandMessageBuilderTest {
    private String correlationID = "1";

    @Test
    public void testCorrelationID() throws JMSException {
        MapMessageMock mockMessage = new MapMessageMock();
        Map<String, String> messageBody = ImmutableMap.of("A", "b");
        Map<String, String> properties = ImmutableMap.of();
        GMPCommandMessageBuilder messageBuilder = new GMPCommandMessageBuilder(correlationID, messageBody, properties);
        MapMessage resultMessage = messageBuilder.constructMessageBody(mockMessage);

        assertEquals(correlationID, resultMessage.getJMSCorrelationID());
    }

    @Test
    public void testMessageConstructionWithNoProperties() throws JMSException {
        MapMessageMock mockMessage = new MapMessageMock();
        Map<String, String> messageBody = ImmutableMap.of("A", "b");
        Map<String, String> properties = ImmutableMap.of();
        GMPCommandMessageBuilder messageBuilder = new GMPCommandMessageBuilder(correlationID, messageBody, properties);
        MapMessage resultMessage = messageBuilder.constructMessageBody(mockMessage);

        assertEquals("b", resultMessage.getString("A"));
        assertFalse(resultMessage.getPropertyNames().hasMoreElements());
    }

    @Test
    public void testMessageConstructionWithNoBodyElements() throws JMSException {
        MapMessageMock mockMessage = new MapMessageMock();
        Map<String, String> messageBody = ImmutableMap.of();
        Map<String, String> properties = ImmutableMap.of("A", "b");

        GMPCommandMessageBuilder messageBuilder = new GMPCommandMessageBuilder(correlationID, messageBody, properties);
        MapMessage resultMessage = messageBuilder.constructMessageBody(mockMessage);

        assertEquals("b", resultMessage.getStringProperty("A"));
        assertFalse(resultMessage.getMapNames().hasMoreElements());
    }
}
