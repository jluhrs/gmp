package edu.gemini.aspen.giapi.util.jms;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.HandlerResponseMessageParser;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.test.MapMessageMock;
import edu.gemini.jms.api.FormatException;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HandlerResponseMessageParserTest {
    private MapMessageMock msg;

    @Before
    public void buildMocks() {
        msg = new MapMessageMock();
    }

    @Test
    public void testReadResponse() throws JMSException {
        msg.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "COMPLETED");

        HandlerResponse response = new HandlerResponseMessageParser(msg).readResponse();
        HandlerResponse referenceResponse = HandlerResponse.get(HandlerResponse.Response.COMPLETED);
        assertEquals(referenceResponse, response);
    }

    @Test
    public void testReadError() throws JMSException {
        msg.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "ERROR");

        HandlerResponse response = new HandlerResponseMessageParser(msg).readResponse();
        HandlerResponse referenceResponse = HandlerResponse.createError("");
        assertEquals(referenceResponse, response);
    }

    @Test
    public void testReadErrorWithMessag() throws JMSException {
        String errorMessage = "Error message";
        msg.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "ERROR");
        msg.setString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, errorMessage);

        HandlerResponse response = new HandlerResponseMessageParser(msg).readResponse();
        HandlerResponse referenceResponse = HandlerResponse.createError(errorMessage);
        assertEquals(referenceResponse, response);
    }

    @Test(expected = FormatException.class)
    public void parseUnknownResponse() throws JMSException {
        msg.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "UNKNOWN");
        new HandlerResponseMessageParser(msg).readResponse();
    }

    @Test(expected = FormatException.class)
    public void parseMessageWithoutResponse() throws JMSException {
        new HandlerResponseMessageParser(msg).readResponse();
    }
    @Test(expected = FormatException.class)
    public void parseNonMapMessage() throws JMSException {
        Message msg = mock(Message.class);
        new HandlerResponseMessageParser(msg);
    }

    @Test(expected = FormatException.class)
    public void testJmsExceptionsConvertedToFormatException() throws JMSException {
        MapMessage msg = mock(MapMessage.class);
        when(msg.getJMSCorrelationID()).thenThrow(new JMSException("Exception"));
        new HandlerResponseMessageParser(msg).readResponse();
    }
}
