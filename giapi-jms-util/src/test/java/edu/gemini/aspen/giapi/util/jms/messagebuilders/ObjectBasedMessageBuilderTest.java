package edu.gemini.aspen.giapi.util.jms.messagebuilders;

import com.google.common.collect.ImmutableMap;
import edu.gemini.aspen.giapi.util.jms.test.MapMessageMock;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ObjectBasedMessageBuilderTest {

    @Test
    public void testMessageConstructionWithNoProperties() throws JMSException {
        MapMessageMock mockMessage = new MapMessageMock();
        Map<String, Object> messageBody = ImmutableMap.<String, Object>of("A", "b");
        Map<String, Object> properties = ImmutableMap.of();
        ObjectBasedMessageBuilder messageBuilder = new ObjectBasedMessageBuilder(messageBody, properties);
        MapMessage resultMessage = messageBuilder.constructMessageBody(mockMessage);

        assertEquals("b", resultMessage.getObject("A"));
        assertFalse(resultMessage.getPropertyNames().hasMoreElements());
    }

    @Test
    public void testMessageConstructionWithNoBodyElements() throws JMSException {
        MapMessageMock mockMessage = new MapMessageMock();
        Map<String, Object> messageBody = ImmutableMap.of();
        Map<String, Object> properties = ImmutableMap.<String, Object>of("A", "b");

        ObjectBasedMessageBuilder messageBuilder = new ObjectBasedMessageBuilder(messageBody, properties);
        MapMessage resultMessage = messageBuilder.constructMessageBody(mockMessage);

        assertEquals("b", resultMessage.getObjectProperty("A"));
        assertFalse(resultMessage.getMapNames().hasMoreElements());
    }
}
