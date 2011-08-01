package edu.gemini.aspen.gmp.services.properties;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.services.PropertyHolder;
import edu.gemini.aspen.gmp.services.core.ServiceException;
import edu.gemini.aspen.gmp.services.core.ServiceType;
import edu.gemini.aspen.gmp.services.jms.JmsServiceRequest;
import org.junit.Before;
import org.junit.Test;

import javax.jms.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PropertyServiceTest {
    private String propertyValue = "localhost";
    private PropertyHolder propertyHolder;
    private String requestedProperty;
    private MessageProducer messageProducer;

    @Before
    public void setUp() throws Exception {
        propertyHolder = mock(PropertyHolder.class);
        requestedProperty = "GMP_HOST_NAME";
    }

    @Test
    public void testGetType() {
        PropertyHolder propertyHolder = mock(PropertyHolder.class);
        assertEquals(ServiceType.PROPERTY_SERVICE, new PropertyService(propertyHolder).getType());
    }

    @Test
    public void testProcess() throws ServiceException, JMSException {
        // TODO The complexity of this mocked tests indicates the need for refactoring
        PropertyService propertyService = new PropertyService(propertyHolder);
        MapMessage requestMessage = mock(MapMessage.class);

        JmsServiceRequest request = mockServiceRequest(requestMessage);
        Destination destination = mockDestination(requestMessage);
        Session session = mockSession(destination);

        TextMessage replyMessage = mockReplyMessage(session);

        propertyService.setJmsSession(session);
        propertyService.process(request);

        assertEquals(propertyValue, replyMessage.getText());
        verify(propertyHolder).getProperty(requestedProperty);
        verify(messageProducer).close();
    }

    private JmsServiceRequest mockServiceRequest(MapMessage msg) throws JMSException {
        JmsServiceRequest request = new JmsServiceRequest(msg);
        when(msg.getString(JmsKeys.GMP_UTIL_PROPERTY)).thenReturn(requestedProperty);
        return request;
    }

    private Destination mockDestination(MapMessage msg) throws JMSException {
        Destination destination = new Destination() {
        };
        when(msg.getJMSReplyTo()).thenReturn(destination);
        return destination;
    }

    private Session mockSession(Destination destination) throws JMSException {
        Session session = mock(Session.class);
        messageProducer = mock(MessageProducer.class);
        when(session.createProducer(destination)).thenReturn(messageProducer);
        return session;
    }

    private TextMessage mockReplyMessage(Session session) throws JMSException {
        TextMessage replyMessage = mock(TextMessage.class);
        when(replyMessage.getText()).thenReturn(propertyValue);
        when(session.createTextMessage(propertyValue)).thenReturn(replyMessage);
        return replyMessage;
    }

    @Test
    public void testEmptyRequestMessage() throws JMSException, ServiceException {
        PropertyService propertyService = new PropertyService(propertyHolder);
        JmsServiceRequest request = new JmsServiceRequest(null);
        // There are really no interactions here, basically we test that the session is not used
        Session session = mock(Session.class);

        propertyService.setJmsSession(session);
        propertyService.process(request);

        verifyZeroInteractions(session);
    }

    @Test(expected = ServiceException.class)
    public void testJmsExceptionConversion() throws JMSException, ServiceException {
        PropertyService propertyService = new PropertyService(propertyHolder);
        MapMessage requestMessage = mock(MapMessage.class);

        JmsServiceRequest request = mockServiceRequest(requestMessage);
        Destination destination = mockDestination(requestMessage);
        Session session = mock(Session.class);
        when(session.createProducer(destination)).thenThrow(new JMSException("Exception"));

        propertyService.setJmsSession(session);
        propertyService.process(request);
    }

    @Test
    public void testNoDestination() throws JMSException, ServiceException {
        PropertyService propertyService = new PropertyService(propertyHolder);
        MapMessage requestMessage = mock(MapMessage.class);

        JmsServiceRequest request = mockServiceRequest(requestMessage);
        Session session = mockSession(null);

        propertyService.setJmsSession(session);
        propertyService.process(request);
    }
}
