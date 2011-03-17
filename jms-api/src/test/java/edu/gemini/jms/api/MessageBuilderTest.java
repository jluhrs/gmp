package edu.gemini.jms.api;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.mockito.Matchers;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class MessageBuilderTest {

    private MessageBuilder messageBuilder = new MessageBuilder();
    private MapMessage message = mock(MapMessage.class);
    private Map<String, Object> messageContent = ImmutableMap.of("item1", (Object) "value1");
    private Map<String, Object> messageProperties = ImmutableMap.of("item1", (Object) "value1");
    private Map<String, String> stringMessageProperties = ImmutableMap.of("item1", "value1");

    @Test
    public void testBuildMessage() throws JMSException {
        messageBuilder.buildMapMessage(message, messageContent);

        verify(message, times(messageContent.size())).setObject(anyString(), Matchers.<Object>anyObject());
    }

    @Test
    public void testSetMessageProperties() throws JMSException {
        messageBuilder.setMessageProperties(message, messageProperties);

        verify(message, times(messageProperties.size())).setObjectProperty(anyString(), Matchers.<Object>anyObject());
    }

    @Test
    public void testSetStringMessageProperties() throws JMSException {
        messageBuilder.setStringMessageProperties(message, stringMessageProperties);

        verify(message, times(messageProperties.size())).setStringProperty(anyString(), anyString());
    }

    @Test(expected = MessagingException.class)
    public void testBuildMessageWithException() throws JMSException {
        doThrow(new JMSException("Exception")).when(message).setObject(anyString(), Matchers.<Object>anyObject());

        messageBuilder.buildMapMessage(message, messageContent);
    }

    @Test(expected = MessagingException.class)
    public void testSetMessagePropertiesWithException() throws JMSException {
        doThrow(new JMSException("Exception")).when(message).setObjectProperty(anyString(), Matchers.<Object>anyObject());

        messageBuilder.setMessageProperties(message, messageProperties);
    }

    @Test(expected = MessagingException.class)
    public void testSetStringMessagePropertiesWithException() throws JMSException {
        doThrow(new JMSException("Exception")).when(message).setStringProperty(anyString(), anyString());

        messageBuilder.setStringMessageProperties(message, stringMessageProperties);
    }
}
