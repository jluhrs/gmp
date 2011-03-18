package edu.gemini.jms.api;

import org.junit.Before;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * Basically a duplicate of MockedJmsArtifactsTestBase but cannot be the same class to avoid
 * introducing a circular dependency
 */
public class JmsArtifactTestBase {
    protected ConnectionFactory connectionFactory;
    protected Session session;

    @Before
    public void setupSession() {
        try {
            mockSessionProducerAndConsumer();
        } catch (JMSException e) {
            // Shouldn't happen as we are mocking
            e.printStackTrace();
        }
    }

    protected void mockSessionProducerAndConsumer() throws JMSException {
        session = mockSessionCreation();

        MessageProducer producer = Mockito.mock(MessageProducer.class);
        Mockito.when(session.createProducer(Matchers.<Destination>anyObject())).thenReturn(producer);
        MessageConsumer consumer = Mockito.mock(MessageConsumer.class);
        Mockito.when(session.createConsumer(Matchers.<Destination>anyObject())).thenReturn(consumer);
    }

    private Session mockSessionCreation() throws JMSException {
        Session session = Mockito.mock(Session.class);
        // Mock connection factory
        connectionFactory = Mockito.mock(ConnectionFactory.class);

        // Mock connection
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(connectionFactory.createConnection()).thenReturn(connection);

        // Mock session
        Mockito.when(connection.createSession(Matchers.anyBoolean(), Matchers.anyInt())).thenReturn(session);
        return session;
    }
}
