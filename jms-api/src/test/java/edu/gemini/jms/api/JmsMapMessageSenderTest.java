package edu.gemini.jms.api;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.mockito.Matchers;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JmsMapMessageSenderTest {

    private JmsProvider provider;

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

    private Session mockSessionProducerAndConsumer(JmsProvider provider) throws JMSException {
        Session session = mockSessionCreation(provider);

        MessageProducer producer = mock(MessageProducer.class);
        when(session.createProducer(Matchers.<Destination>anyObject())).thenReturn(producer);
        MessageConsumer consumer = mock(MessageConsumer.class);
        when(session.createConsumer(Matchers.<Destination>anyObject())).thenReturn(consumer);

        return session;
    }

    private Session mockSessionCreation(JmsProvider provider) throws JMSException {
        Session session = mock(Session.class);
        // Mock connection factory
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        when(provider.getConnectionFactory()).thenReturn(connectionFactory);

        // Mock connection
        Connection connection = mock(Connection.class);
        when(connectionFactory.createConnection()).thenReturn(connection);

        // Mock session
        when(connection.createSession(anyBoolean(), anyInt())).thenReturn(session);
        return session;
    }
}
