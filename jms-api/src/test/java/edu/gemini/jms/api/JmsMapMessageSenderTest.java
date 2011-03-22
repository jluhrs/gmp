package edu.gemini.jms.api;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JmsMapMessageSenderTest extends JmsArtifactTestBase {

    @Test(expected = MessagingException.class)
    public void testSendMapMessageWhenNotConnected() {
        MapMessageBuilder mapMessageBuilder = mock(MapMessageBuilder.class);
        JmsMapMessageSender sender = new JmsMapMessageSender("GMP.TOPIC");
        DestinationData destination = new DestinationData("GMP.TOPIC", DestinationType.TOPIC);

        sender.sendMapMessage(destination, mapMessageBuilder);
    }

    @Test
    public void testSendStringBasedMapMessage() throws JMSException {
        mockSessionProducerAndConsumer();
        MapMessageBuilder mapMessageBuilder = mock(MapMessageBuilder.class);

        JmsMapMessageSender sender = new JmsMapMessageSender("GMP.TOPIC");
        JmsProvider provider = mock(JmsProvider.class);
        when(provider.getConnectionFactory()).thenReturn(connectionFactory);

        MapMessage mapMessage = mock(MapMessage.class);
        when(session.createMapMessage()).thenReturn(mapMessage);
        
        sender.startJms(provider);

        Map<String,String> message = ImmutableMap.of();
        Map<String,String> properties = ImmutableMap.of();
        DestinationData destinationData = new DestinationData("GMP.TOPIC", DestinationType.TOPIC);

        assertNotNull(sender.sendMapMessage(destinationData, mapMessageBuilder));
    }

}
