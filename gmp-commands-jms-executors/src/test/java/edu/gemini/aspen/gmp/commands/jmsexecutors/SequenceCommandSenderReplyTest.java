package edu.gemini.aspen.gmp.commands.jmsexecutors;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Test;
import org.mockito.Matchers;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SequenceCommandSenderReplyTest {
    private String clientData = JmsKeys.GW_COMMAND_TOPIC;

    @Test
    public void testBuildResponseCompleted() throws JMSException {
        SequenceCommandSenderReply senderReply = new SequenceCommandSenderReply(clientData);

        MapMessage reply = mock(MapMessage.class);
        when(reply.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenReturn("COMPLETED");

        HandlerResponse response = senderReply.buildResponse(reply);
        assertEquals(HandlerResponse.Response.COMPLETED, response.getResponse());
        assertNull(response.getMessage());
    }

    @Test
    public void testBuildResponseError() throws JMSException {
        SequenceCommandSenderReply senderReply = new SequenceCommandSenderReply(clientData);

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
        SequenceCommandSenderReply senderReply = new SequenceCommandSenderReply(clientData);

        Message reply = mock(Message.class);

        HandlerResponse response = senderReply.buildResponse(reply);
        assertEquals(HandlerResponse.Response.NOANSWER, response.getResponse());
    }

    @Test
    public void testSend() throws JMSException {
        JmsProvider provider = mock(JmsProvider.class);
        mockMessageAndDestinations(provider);

        SequenceCommandSenderReply senderReply = new SequenceCommandSenderReply(clientData);
        senderReply.startJms(provider);

        ActionMessage actionMessage = createActionToSend();
        HandlerResponse response = senderReply.send(actionMessage);

        assertEquals(HandlerResponse.Response.NOANSWER, response.getResponse());
    }

    private ActionMessage createActionToSend() {
        JmsActionMessageBuilder messageBuilder = new JmsActionMessageBuilder();
        Action action = new Action(new Command(SequenceCommand.DATUM,
                Activity.START, emptyConfiguration()), new CompletionListenerMock());

        return messageBuilder.buildActionMessage(action);
    }

    @Test(expected = SequenceCommandException.class)
    public void testErrorWhileSending() throws JMSException {
        JmsProvider provider = mock(JmsProvider.class);
        Session session = mockSessionCreation(provider);
        when(session.createMapMessage()).thenThrow(new JMSException(""));

        SequenceCommandSenderReply senderReply = new SequenceCommandSenderReply(clientData);
        senderReply.startJms(provider);

        ActionMessage actionMessage = createActionToSend();
        senderReply.send(actionMessage);
    }

    private Session mockMessageAndDestinations(JmsProvider provider) throws JMSException {
        Session session = mockSessionCreation(provider);

        MapMessage mapMessage = mock(MapMessage.class);
        when(session.createMapMessage()).thenReturn(mapMessage);
        MessageProducer producer = mock(MessageProducer.class);
        when(session.createProducer(Matchers.<Destination>anyObject())).thenReturn(producer);
        MessageConsumer consumer = mock(MessageConsumer.class);
        when(session.createConsumer(Matchers.<Destination>anyObject())).thenReturn(consumer);

        return session;
    }

    private Session mockSessionCreation(JmsProvider provider) throws JMSException {
        Session session = mock(Session.class);
        // Mock connection factory
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        when(provider.getConnectionFactory()).thenReturn(connectionFactory);

        // Mock connection
        Connection connection = mock(Connection.class);
        when(connectionFactory.createConnection()).thenReturn(connection);

        // Mock session
        when(connection.createSession(anyBoolean(), anyInt())).thenReturn(session);
        return session;
    }
}
