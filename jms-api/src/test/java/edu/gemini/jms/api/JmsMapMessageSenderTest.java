package edu.gemini.jms.api;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.mockito.Matchers;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Topic;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JmsMapMessageSenderTest extends JmsArtifactTestBase {

    protected DestinationData destinationData = new DestinationData("GMP.TOPIC", DestinationType.TOPIC);

    @Test(expected = MessagingException.class)
    public void testSendMapMessageWhenNotConnected() {
        MapMessageBuilder mapMessageBuilder = mock(MapMessageBuilder.class);
        JmsMapMessageSender sender = new JmsMapMessageSender("GMP.TOPIC");

        sender.sendMapMessage(destinationData, mapMessageBuilder);
    }

    @Test
    public void testSendStringBasedMapMessage() throws JMSException {
        mockSessionProducerAndConsumer();

        JmsMapMessageSender sender = new JmsMapMessageSender("GMP.TOPIC");

        sender.startJms(provider);

        final Map<String,String> messageBody = ImmutableMap.of("x:A", "1");
        final Map<String,String> messageProperties = ImmutableMap.of("HANDLER_RESPONSE", "COMPLETED");
        final AtomicBoolean called = new AtomicBoolean(false);

        MapMessageBuilder mapMessageBuilder = new AbstractMapMessageBuilder() {
            @Override
            public MapMessage constructMessageBody(MapMessage message) throws JMSException {
                super.setStringBasedMessageBody(message, messageBody);
                super.setStringBasedMessageBody(message, messageProperties);

                // Check that this is called
                called.set(true);
                return message;
            }
        };

        MapMessage sentMessage = sender.sendMapMessage(destinationData, mapMessageBuilder);

        assertNotNull(sentMessage);
        verify(producer).send(Matchers.<Topic>anyObject(), eq(mapMessage));

        assertTrue(called.get());
    }

}
