package edu.gemini.aspen.gmp.status.simulator;

import edu.gemini.jms.api.JmsProvider;
import org.junit.Test;
import org.mockito.Matchers;

import javax.jms.*;
import javax.xml.bind.JAXBException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

public class StatusSimulatorTest {
    private StatusSimulator component;
    protected ConnectionFactory connectionFactory;
    protected Session session;
    protected JmsProvider provider;
    protected MessageProducer producer;
    protected MessageConsumer consumer;
    protected MapMessage mapMessage;

    @Test
    public void testCreation() throws InterruptedException, JAXBException {
        component = new StatusSimulator(new SimulatorConfiguration(getClass().getResourceAsStream("status-simulator.xml")));
        assertNotNull(component);
    }

    @Test
    public void testStartJMSProvider() throws InterruptedException, JAXBException, JMSException {
        provider = mock(JmsProvider.class);
        mockSessionProducerAndConsumer();

        component = new StatusSimulator(new SimulatorConfiguration(getClass().getResourceAsStream("status-simulator.xml")));
        component.startJms(provider);
        verify(session.createProducer(any(Destination.class)), times(2));
    }

    protected void mockSessionProducerAndConsumer() throws JMSException {
        session = mockSessionCreation();

        producer = mock(MessageProducer.class);
        when(session.createProducer(Matchers.<Destination>anyObject())).thenReturn(producer);
        consumer = mock(MessageConsumer.class);
        when(session.createConsumer(Matchers.<Destination>anyObject())).thenReturn(consumer);

        Queue queue = mock(Queue.class);
        when(session.createQueue(anyString())).thenReturn(queue);

        Topic topic = mock(Topic.class);
        when(session.createTopic(anyString())).thenReturn(topic);

        mapMessage = mock(MapMessage.class);
        when(session.createMapMessage()).thenReturn(mapMessage);

        when(provider.getConnectionFactory()).thenReturn(connectionFactory);
    }

    private Session mockSessionCreation() throws JMSException {
        Session session = mock(Session.class);
        // Mock connection factory
        connectionFactory = mock(ConnectionFactory.class);

        // Mock connection
        Connection connection = mock(Connection.class);
        when(connectionFactory.createConnection()).thenReturn(connection);

        // Mock session
        when(connection.createSession(Matchers.anyBoolean(), Matchers.anyInt())).thenReturn(session);
        return session;
    }

}