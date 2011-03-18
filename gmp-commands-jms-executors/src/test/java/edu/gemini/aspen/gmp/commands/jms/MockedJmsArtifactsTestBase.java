package edu.gemini.aspen.gmp.commands.jms;

import edu.gemini.jms.api.JmsProvider;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import static org.mockito.Mockito.when;

public class MockedJmsArtifactsTestBase {
    protected ConnectionFactory connectionFactory;
    protected Session session;
    protected JmsProvider provider;
    protected MessageProducer producer;
    protected MessageConsumer consumer;

    public void createMockedObjects() {
        provider = Mockito.mock(JmsProvider.class);
        try {
            mockSessionProducerAndConsumer();
            when(provider.getConnectionFactory()).thenReturn(connectionFactory);
        } catch (JMSException e) {
            // Shouldn't happen as we are mocking
            e.printStackTrace();
        }
    }

    protected void mockSessionProducerAndConsumer() throws JMSException {
        session = mockSessionCreation();

        producer = Mockito.mock(MessageProducer.class);
        when(session.createProducer(Matchers.<Destination>anyObject())).thenReturn(producer);
        consumer = Mockito.mock(MessageConsumer.class);
        when(session.createConsumer(Matchers.<Destination>anyObject())).thenReturn(consumer);

        MapMessage mapMessage = Mockito.mock(MapMessage.class);
        when(session.createMapMessage()).thenReturn(mapMessage);
    }

    private Session mockSessionCreation() throws JMSException {
        Session session = Mockito.mock(Session.class);
        // Mock connection factory
        connectionFactory = Mockito.mock(ConnectionFactory.class);

        // Mock connection
        Connection connection = Mockito.mock(Connection.class);
        when(connectionFactory.createConnection()).thenReturn(connection);

        // Mock session
        when(connection.createSession(Matchers.anyBoolean(), Matchers.anyInt())).thenReturn(session);
        return session;
    }
}
