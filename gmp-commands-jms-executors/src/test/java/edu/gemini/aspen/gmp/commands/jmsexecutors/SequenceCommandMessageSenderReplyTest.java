package edu.gemini.aspen.gmp.commands.jmsexecutors;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SequenceCommandMessageSenderReplyTest {
    private String clientData = JmsKeys.GW_COMMAND_TOPIC;

    @Test
    public void testBuildResponseCompleted() throws JMSException {
        SequenceCommandMessageSenderReply senderReply = new SequenceCommandMessageSenderReply(clientData);

        MapMessage reply = mock(MapMessage.class);
        when(reply.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenReturn("COMPLETED");

        HandlerResponse response = senderReply.buildResponse(reply);
        assertEquals(HandlerResponse.Response.COMPLETED, response.getResponse());
        assertNull(response.getMessage());
    }

    @Test
    public void testBuildResponseError() throws JMSException {
        SequenceCommandMessageSenderReply senderReply = new SequenceCommandMessageSenderReply(clientData);

        MapMessage reply = mock(MapMessage.class);
        when(reply.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenReturn("ERROR");
        String errorMessage = "Error Message";
        when(reply.getString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY)).thenReturn(errorMessage);

        HandlerResponse response = senderReply.buildResponse(reply);
        assertEquals(HandlerResponse.Response.ERROR, response.getResponse());
        assertEquals(errorMessage, response.getMessage());
    }

    @Test
    public void testBuildResponseNoMessage() throws JMSException {
        SequenceCommandMessageSenderReply senderReply = new SequenceCommandMessageSenderReply(clientData);

        Message reply = mock(Message.class);

        HandlerResponse response = senderReply.buildResponse(reply);
        assertEquals(HandlerResponse.Response.NOANSWER, response.getResponse());
    }

}
