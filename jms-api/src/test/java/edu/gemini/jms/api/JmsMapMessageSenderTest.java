package edu.gemini.jms.api;

import com.google.common.collect.ImmutableMap;
import edu.gemini.aspen.giapitestsupport.jms.JmsArtifactTestBase;
import org.junit.Test;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JmsMapMessageSenderTest extends JmsArtifactTestBase {

    @Test
    public void testSendMapMessage() {
        JmsMapMessageSender sender = new JmsMapMessageSender("GMP.TOPIC");
        Map<String,Object> message = ImmutableMap.of();
        Map<String,Object> properties = ImmutableMap.of();
        DestinationData destination = new DestinationData("GMP.TOPIC", DestinationType.TOPIC);

        sender.sendMapMessage(destination, message, properties);
    }

    @Test
    public void testSendStringBasedMapMessage() throws JMSException {
        JmsMapMessageSender sender = new JmsMapMessageSender("GMP.TOPIC");
        provider = mock(JmsProvider.class);

        Session session = mockSessionProducerAndConsumer(provider);

        MapMessage mapMessage = mock(MapMessage.class);
        when(session.createMapMessage()).thenReturn(mapMessage);
        
        sender.startJms(provider);

        Map<String,String> message = ImmutableMap.of();
        Map<String,String> properties = ImmutableMap.of();
        Destination destination = mock(Destination.class);

        assertNotNull(sender.sendStringBasedMapMessage(destination, message, properties,
                JmsMapMessageSender.MapMessageCreator.NoReplyCreator));
    }

}
