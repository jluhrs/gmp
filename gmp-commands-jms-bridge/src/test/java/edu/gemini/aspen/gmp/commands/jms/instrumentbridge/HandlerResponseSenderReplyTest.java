package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.test.MapMessageMock;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class HandlerResponseSenderReplyTest {
    private String clientData = JmsKeys.GW_COMMAND_TOPIC;

    @Test
    public void testBuildResponseCompleted() throws JMSException {
        HandlerResponseSenderReply senderReply = new HandlerResponseSenderReply(clientData);

        MapMessageMock reply = new MapMessageMock();
        reply.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "COMPLETED");

        HandlerResponse response = senderReply.buildResponse(reply);
        assertEquals(HandlerResponse.Response.COMPLETED, response.getResponse());
        assertFalse(response.hasErrorMessage());
    }

    @Test
    public void testBuildResponseError() throws JMSException {
        HandlerResponseSenderReply senderReply = new HandlerResponseSenderReply(clientData);

        MapMessageMock reply = new MapMessageMock();
        reply.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "ERROR");

        String errorMessage = "Error Message";
        reply.setString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, errorMessage);

        HandlerResponse response = senderReply.buildResponse(reply);
        assertEquals(HandlerResponse.Response.ERROR, response.getResponse());
        assertEquals(errorMessage, response.getMessage());
    }

    @Test
    public void testBuildResponseNoMessage() throws JMSException {
        HandlerResponseSenderReply senderReply = new HandlerResponseSenderReply(clientData);

        Message reply = mock(Message.class);

        HandlerResponse response = senderReply.buildResponse(reply);
        assertEquals(HandlerResponse.Response.NOANSWER, response.getResponse());
    }

}
