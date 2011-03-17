package edu.gemini.aspen.giapitestsupport.jms;

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

import static org.mockito.Mockito.when;

public class MockedJmsArtifactsTestBase {
    protected ConnectionFactory connectionFactory;
    protected Session session;
    protected JmsProvider provider;

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
