package edu.gemini.aspen.giapi.util.jms;


import edu.gemini.aspen.giapi.commands.*;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.copy;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import static org.mockito.Mockito.*;


/**
 * Unit tests for the MessageBuilder class
 */
public class MessageBuilderTest {
    private Session _mockedSession;

    @Before
    public void setUp() throws JMSException {
        _mockedSession = mock(Session.class);
        when(_mockedSession.createMapMessage()).thenReturn(new ActiveMQMapMessage());
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

        MapMessage mm = new ActiveMQMapMessage();

        try {
            MessageBuilder.buildHandlerResponse(mm);
        } catch (JMSException e) {
            assertEquals(MessageBuilder.InvalidResponseTypeMessage(), e.getMessage());
        }


    }

    @Test
    public void testInvalidResponseType() {

        MapMessage mm = new ActiveMQMapMessage();


        try {
            mm.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "INVALID");
            MessageBuilder.buildHandlerResponse(mm);
        } catch (JMSException e) {
            assertEquals(MessageBuilder.InvalidResponseTypeMessage("INVALID"), e.getMessage());
        }

    }

    @Test
    public void testValidHandlerResponse() throws JMSException {
        MapMessage mm = new ActiveMQMapMessage();


        mm.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "STARTED");

        HandlerResponse response = MessageBuilder.buildHandlerResponse(mm);
        assertEquals(HandlerResponse.STARTED, response);


    }

    @Test
    public void testErrorHandlerResponse() throws JMSException {
        MapMessage mm = new ActiveMQMapMessage();

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
}
