package edu.gemini.aspen.giapi.util.jms;


import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionInformation;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.DefaultConfiguration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.junit.Before;
import org.junit.Test;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Set;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
    public void testBuildIntegerStatusItemMessage() throws JMSException {
        String statusName = "X.val1";
        Integer statusValue = 1;
        StatusItem statusItem = new BasicStatus<Integer>(statusName, statusValue);

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
        Double statusValue = 1.0;
        StatusItem statusItem = new BasicStatus<Double>(statusName, statusValue);

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
        Float statusValue = 1.0f;
        StatusItem statusItem = new BasicStatus<Float>(statusName, statusValue);

        BytesMessage mockedMessage = mock(BytesMessage.class);
        when(_mockedSession.createBytesMessage()).thenReturn(mockedMessage);

        BytesMessage msg = (BytesMessage) MessageBuilder.buildStatusItemMessage(_mockedSession, statusItem);

        verify(msg).writeByte((byte)2);
        verify(msg).writeUTF(statusName);
        verify(msg).writeFloat(statusValue);
    }

    @Test
    public void testInvalidConfigurationMessage() {
        // Rather test that the string is there than the actual content
        assertFalse(MessageBuilder.InvalidConfigurationMessage().isEmpty());
    }

    @Test
    public void testInvalidActivityMessage() {
        // Rather test that the string is there than the actual content
        assertTrue(MessageBuilder.InvalidActivityMessage("msg").contains("msg"));
    }

    @Test
    public void testBuildStatusNames() throws JMSException {
        BytesMessage m = mock(BytesMessage.class);
        when(m.readInt()).thenReturn(2);
        when(m.readUTF()).thenReturn("b", "a");

        Set<String> statusNames = MessageBuilder.buildStatusNames(m);
        
        assertFalse(statusNames.isEmpty());
        assertTrue(statusNames.contains("a"));
        assertTrue(statusNames.contains("b"));
    }

    @Test
    public void testBuildStatusNamesWithNoValidMessage() throws JMSException {
        Message m = mock(Message.class);

        Set<String> statusNames = MessageBuilder.buildStatusNames(m);
        
        assertTrue(statusNames.isEmpty());
    }

    @Test
    public void testBuildStringStatusItemMessage() throws JMSException {
        String statusName = "X.val1";
        String statusValue = "status";
        StatusItem statusItem = new BasicStatus<String>(statusName, statusValue);

        BytesMessage mockedMessage = mock(BytesMessage.class);
        when(_mockedSession.createBytesMessage()).thenReturn(mockedMessage);

        BytesMessage msg = (BytesMessage) MessageBuilder.buildStatusItemMessage(_mockedSession, statusItem);

        verify(msg).writeByte((byte)3);
        verify(msg).writeUTF(statusName);
        verify(msg).writeUTF(statusValue);
    }

    @Test
    public void testBuildCompletionInformationOnAcceptedResponse() throws JMSException {
        mm.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY, HandlerResponse.Response.ACCEPTED.toString());
        mm.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, SequenceCommand.APPLY.toString());
        mm.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, Activity.PRESET.toString());
        CompletionInformation completionInformation = MessageBuilder.buildCompletionInformation(mm);

        assertEquals(new CompletionInformation(HandlerResponse.ACCEPTED, new Command(SequenceCommand.APPLY, Activity.PRESET, emptyConfiguration())), completionInformation);
    }

    @Test
    public void testBuildCompletionInformationOnAcceptedResponseWithConfiguration() throws JMSException {
        mm.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY, HandlerResponse.Response.ACCEPTED.toString());
        mm.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, SequenceCommand.APPLY.toString());
        mm.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, Activity.PRESET.toString());

        mm.setString("x:A", "1");
        mm.setString("x:B", "2");
        CompletionInformation completionInformation = MessageBuilder.buildCompletionInformation(mm);

        Configuration config = DefaultConfiguration.configurationBuilder()
                .withPath(configPath("x:A"), "1")
                .withPath(configPath("x:B"), "2")
                .build();
        assertEquals(new CompletionInformation(HandlerResponse.ACCEPTED, new Command(SequenceCommand.APPLY, Activity.PRESET, config)), completionInformation);
    }

    @Test
    public void testBuildCompletionInformationOnErrorResponse() throws JMSException {
        mm.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY, HandlerResponse.Response.ERROR.toString());
        mm.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, "Error message");
        mm.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, SequenceCommand.APPLY.toString());
        mm.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, Activity.PRESET.toString());
        CompletionInformation completionInformation = MessageBuilder.buildCompletionInformation(mm);

        assertEquals(new CompletionInformation(HandlerResponse.createError("Error message"), new Command(SequenceCommand.APPLY, Activity.PRESET, emptyConfiguration())), completionInformation);
    }

    @Test(expected = JMSException.class)
    public void testBuildCompletionInformationWithBadHandlerResponse() throws JMSException {
        mm.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "BADRESPONSE");
        MessageBuilder.buildCompletionInformation(mm);
    }

    @Test(expected = JMSException.class)
    public void testBuildCompletionInformationWithBadSequenceCommand() throws JMSException {
        mm.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY, HandlerResponse.Response.ERROR.toString());
         mm.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, "BADCOMMAND");
        MessageBuilder.buildCompletionInformation(mm);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildCompletionInformationWithoutHandlerResponse() throws JMSException {
        mm.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY, null);
        mm.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, SequenceCommand.APPLY.toString());
        mm.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, Activity.PRESET.toString());
        MessageBuilder.buildCompletionInformation(mm);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildCompletionInformationWithoutSequenceCommand() throws JMSException {
        mm.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY, HandlerResponse.Response.ACCEPTED.toString());
        mm.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, null);
        mm.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, Activity.PRESET.toString());
        MessageBuilder.buildCompletionInformation(mm);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildCompletionInformationWithoutActivity() throws JMSException {
        mm.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY, HandlerResponse.Response.ACCEPTED.toString());
        mm.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, SequenceCommand.APPLY.toString());
        mm.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, null);
        MessageBuilder.buildCompletionInformation(mm);
    }

    @Test(expected = JMSException.class)
    public void testBuildCompletionInformationOnNonMapMessage() throws JMSException {
        Message m = mock(Message.class);
        MessageBuilder.buildCompletionInformation(m);
    }

}
