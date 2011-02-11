package edu.gemini.aspen.giapi.util.jms;


import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.junit.Before;
import org.junit.Test;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.copy;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for the MessageBuilder class
 */
public class MessageBuilderTest {
    private Session _mockedSession;
    private MapMessage mm;

    @Before
    public void setUp() throws JMSException {
        _mockedSession = mock(Session.class);
        when(_mockedSession.createMapMessage()).thenReturn(new ActiveMQMapMessage());
        mm = new ActiveMQMapMessage();
    }

    @Test
    public void testWrongMessageTypeToBuildHandlerResponse() {
        try {
            MessageBuilder.buildHandlerResponse(new ActiveMQBytesMessage());
        } catch (JMSException e) {
            assertEquals(MessageBuilder.InvalidHandlerResponseMessage(), e.getMessage());
        }
    }

    @Test
    public void testNullResponseType() {
        try {
            MessageBuilder.buildHandlerResponse(mm);
        } catch (JMSException e) {
            assertEquals(MessageBuilder.InvalidResponseTypeMessage(), e.getMessage());
        }
    }

    @Test
    public void testInvalidResponseType() {
        try {
            mm.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "INVALID");
            MessageBuilder.buildHandlerResponse(mm);
        } catch (JMSException e) {
            assertEquals(MessageBuilder.InvalidResponseTypeMessage("INVALID"), e.getMessage());
        }
    }

    @Test
    public void testValidHandlerResponse() throws JMSException {
        mm.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "STARTED");

        HandlerResponse response = MessageBuilder.buildHandlerResponse(mm);
        assertEquals(HandlerResponse.STARTED, response);
    }

    @Test
    public void testErrorHandlerResponse() throws JMSException {
        mm.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "ERROR");
        mm.setString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, "Error Message");
        HandlerResponse response = MessageBuilder.buildHandlerResponse(mm);
        assertEquals(HandlerResponse.createError("Error Message"), response);
    }

    @Test
    public void testCreateAcceptedHandlerResponse() throws JMSException {
        Session mockedSession = mock(Session.class);
        when(mockedSession.createMapMessage()).thenReturn(new ActiveMQMapMessage());

        HandlerResponse response = HandlerResponse.ACCEPTED;
        MapMessage m = (MapMessage) MessageBuilder.buildHandlerResponseMessage(mockedSession, response);

        assertEquals(HandlerResponse.ACCEPTED.getResponse().name(), m.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY));
        //we should not have any error messages
        assertNull(m.getString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY));
    }

    @Test
    public void testCreateErrorHandlerResponse() throws JMSException {
        HandlerResponse response = HandlerResponse.createError("An error");
        MapMessage m = (MapMessage) MessageBuilder.buildHandlerResponseMessage(_mockedSession, response);

        assertEquals(response.getResponse().name(), m.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY));
        assertEquals(response.getMessage(), m.getString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY));
    }

    @Test
    public void testCreateErrorHandlerResponseWithoutMessage() throws JMSException {
        HandlerResponse response = HandlerResponse.createError(null);
        MapMessage m = (MapMessage) MessageBuilder.buildHandlerResponseMessage(_mockedSession, response);

        assertEquals(response.getResponse().name(), m.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY));
        assertNull(m.getString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY));
    }

    @Test
    public void testBuildCompletionInformationMessage() throws JMSException {
        Configuration config = copy(emptyConfiguration())
                .withPath(configPath("gpi:dc.value1"), "one")
                .withPath(configPath("gpi:dc.value2"), "two")
                .build();

        CompletionInformation ci = new CompletionInformation(
                HandlerResponse.STARTED,
                SequenceCommand.INIT,
                Activity.START,
                config
        );

        MapMessage m = (MapMessage) MessageBuilder.buildCompletionInformationMessage(_mockedSession, ci);

        assertEquals(HandlerResponse.STARTED.getResponse().name(), m.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY));
        assertEquals(SequenceCommand.INIT.name(), m.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY));
        assertEquals(Activity.START.name(), m.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY));

        for (ConfigPath path : config.getKeys()) {
            assertEquals(config.getValue(path), m.getString(path.getName()));
        }
    }

    @Test
    public void testBuildIntegerStatusItemMessage() throws JMSException {
        String statusName = "X.val1";
        Integer statusValue = Integer.valueOf(1);
        StatusItem statusItem = new BasicStatus(statusName, statusValue);

        BytesMessage mockedMessage = mock(BytesMessage.class);
        when(_mockedSession.createBytesMessage()).thenReturn(mockedMessage);

        BytesMessage msg = (BytesMessage) MessageBuilder.buildStatusItemMessage(_mockedSession, statusItem);

        verify(msg).writeByte((byte)0);
        verify(msg).writeUTF(statusName);
        verify(msg).writeInt(statusValue);
    }

    @Test
    public void testBuildDoubleStatusItemMessage() throws JMSException {
        String statusName = "X.val1";
        Double statusValue = Double.valueOf(1);
        StatusItem statusItem = new BasicStatus(statusName, statusValue);

        BytesMessage mockedMessage = mock(BytesMessage.class);
        when(_mockedSession.createBytesMessage()).thenReturn(mockedMessage);

        BytesMessage msg = (BytesMessage) MessageBuilder.buildStatusItemMessage(_mockedSession, statusItem);

        verify(msg).writeByte((byte)1);
        verify(msg).writeUTF(statusName);
        verify(msg).writeDouble(statusValue);
    }

    @Test
    public void testBuildFloatStatusItemMessage() throws JMSException {
        String statusName = "X.val1";
        Float statusValue = Float.valueOf(1);
        StatusItem statusItem = new BasicStatus(statusName, statusValue);

        BytesMessage mockedMessage = mock(BytesMessage.class);
        when(_mockedSession.createBytesMessage()).thenReturn(mockedMessage);

        BytesMessage msg = (BytesMessage) MessageBuilder.buildStatusItemMessage(_mockedSession, statusItem);

        verify(msg).writeByte((byte)2);
        verify(msg).writeUTF(statusName);
        verify(msg).writeFloat(statusValue);
    }

    @Test
    public void testBuildStringStatusItemMessage() throws JMSException {
        String statusName = "X.val1";
        String statusValue = "status";
        StatusItem statusItem = new BasicStatus(statusName, statusValue);

        BytesMessage mockedMessage = mock(BytesMessage.class);
        when(_mockedSession.createBytesMessage()).thenReturn(mockedMessage);

        BytesMessage msg = (BytesMessage) MessageBuilder.buildStatusItemMessage(_mockedSession, statusItem);

        verify(msg).writeByte((byte)3);
        verify(msg).writeUTF(statusName);
        verify(msg).writeUTF(statusValue);
    }

}
