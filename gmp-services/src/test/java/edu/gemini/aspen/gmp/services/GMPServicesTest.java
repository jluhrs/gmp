package edu.gemini.aspen.gmp.services;

import edu.gemini.aspen.gmp.services.properties.XMLFileBasedPropertyHolder;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Test;
import org.mockito.Matchers;

import javax.jms.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests gor the GMPServices class
 */
public class GMPServicesTest {
    @Test
    public void testStartServices() throws JMSException {
        JmsProvider provider = mock(JmsProvider.class);
        mockJMSObjects(provider);

        XMLFileBasedPropertyHolder propertyHolder = new XMLFileBasedPropertyHolder(GMPServicesTest.class.getResource("gmp-properties.xml").getFile());

        GMPServices services = new GMPServices(provider, propertyHolder);
        assertNotNull(services);

        services.startServices();
    }

    private void mockJMSObjects(JmsProvider provider) throws JMSException {
        Session session = mock(Session.class);
        // Mock connection factory
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);

        // Mock connection
        Connection connection = mock(Connection.class);
        when(connectionFactory.createConnection()).thenReturn(connection);

        // Mock session
        when(connection.createSession(Matchers.anyBoolean(), Matchers.anyInt())).thenReturn(session);

        MessageConsumer consumer = mock(MessageConsumer.class);

        when(session.createConsumer(Matchers.any(Destination.class))).thenReturn(consumer);
        when(provider.getConnectionFactory()).thenReturn(connectionFactory);
    }
}
