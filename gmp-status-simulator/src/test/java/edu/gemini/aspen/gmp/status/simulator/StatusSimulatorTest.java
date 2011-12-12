package edu.gemini.aspen.gmp.status.simulator;

import edu.gemini.jms.api.JmsProvider;
import org.junit.Test;
import org.mockito.Matchers;

import javax.jms.*;
import javax.xml.bind.JAXBException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

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

    /*@Test
    public void testOneSimulation() throws InterruptedException, JAXBException, JMSException {
        provider = mock(JmsProvider.class);
        mockSessionProducerAndConsumer();

        component = new StatusSimulator(new SimulatorConfiguration(getClass().getResourceAsStream("status-simulator.xml")));
        component.startJms(provider);
        component.simulateOnce();
        component.stopJms();
        verifyZeroInteractions(producer, times(2));
    }*/

    @Test
    public void testStartJMSProvider() throws InterruptedException, JAXBException, JMSException {
        provider = mock(JmsProvider.class);
        mockSessionProducerAndConsumer();

        component = new StatusSimulator(new SimulatorConfiguration(getClass().getResourceAsStream("status-simulator.xml")));
        component.startJms(provider);
        verify(session, times(2)).createProducer(any(Destination.class));
    }

    @Test
    public void testStopJMSProvider() throws InterruptedException, JAXBException, JMSException {
        provider = mock(JmsProvider.class);
        mockSessionProducerAndConsumer();

        component = new StatusSimulator(new SimulatorConfiguration(getClass().getResourceAsStream("status-simulator.xml")));
        component.startJms(provider);
        component.stopJms();
        verify(session, times(2)).close();
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