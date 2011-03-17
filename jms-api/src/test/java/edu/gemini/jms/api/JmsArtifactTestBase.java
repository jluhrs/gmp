package edu.gemini.jms.api;

import edu.gemini.jms.api.JmsProvider;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class JmsArtifactTestBase {
    protected JmsProvider provider;

    protected Session mockSessionProducerAndConsumer(JmsProvider provider) throws JMSException {
        Session session = mockSessionCreation(provider);

        MessageProducer producer = Mockito.mock(MessageProducer.class);
        Mockito.when(session.createProducer(Matchers.<Destination>anyObject())).thenReturn(producer);
        MessageConsumer consumer = Mockito.mock(MessageConsumer.class);
        Mockito.when(session.createConsumer(Matchers.<Destination>anyObject())).thenReturn(consumer);

        return session;
    }

    private Session mockSessionCreation(JmsProvider provider) throws JMSException {
        Session session = Mockito.mock(Session.class);
        // Mock connection factory
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Mockito.when(provider.getConnectionFactory()).thenReturn(connectionFactory);

        // Mock connection
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(connectionFactory.createConnection()).thenReturn(connection);

        // Mock session
        Mockito.when(connection.createSession(Matchers.anyBoolean(), Matchers.anyInt())).thenReturn(session);
        return session;
    }
}
