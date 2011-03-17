package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;

import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.jms.instrumentbridge.CompletionInfoListener;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class CompletionInfoListenerTest {
    private MapMessage message;
    private JmsProvider jmsProvider;
    private CommandUpdater commandUpdater;

    @Before
    public void setUp() throws Exception {
        message = mock(MapMessage.class);
        jmsProvider = mock(JmsProvider.class);
        commandUpdater = mock(CommandUpdater.class);
    }

    @Test
    public void testOCSUpdate() throws JMSException {
        CompletionInfoListener listener = new CompletionInfoListener(commandUpdater, jmsProvider);
        when(message.propertyExists(JmsKeys.GMP_ACTIONID_PROP)).thenReturn(true);
        when(message.getIntProperty(JmsKeys.GMP_ACTIONID_PROP)).thenReturn(1);
        when(message.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenReturn(HandlerResponse.Response.COMPLETED.toString());

        listener.onMessage(message);

        verify(commandUpdater).updateOcs(1, HandlerResponse.get(HandlerResponse.Response.COMPLETED));
    }

    @Test(expected = SequenceCommandException.class)
    public void testExceptionOnMessageQuerying() throws JMSException {
        CompletionInfoListener listener = new CompletionInfoListener(commandUpdater, jmsProvider);
        when(message.propertyExists(JmsKeys.GMP_ACTIONID_PROP)).thenReturn(true);
        when(message.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenThrow(new JMSException("exception"));

        listener.onMessage(message);
    }

    @Test
    public void testNoMapMessage() throws JMSException {
        CompletionInfoListener listener = new CompletionInfoListener(commandUpdater, jmsProvider);
        Message message = mock(Message.class);

        listener.onMessage(message);

        verifyZeroInteractions(commandUpdater);
    }

    @Test
    public void testLifeCycle() throws JMSException {
        Session session = mock(Session.class);
        // Mock connection factory
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        when(jmsProvider.getConnectionFactory()).thenReturn(connectionFactory);

        // Mock connection
        Connection connection = mock(Connection.class);
        when(connectionFactory.createConnection()).thenReturn(connection);

        // Mock session
        when(connection.createSession(anyBoolean(), anyInt())).thenReturn(session);

        CompletionInfoListener listener = new CompletionInfoListener(commandUpdater, jmsProvider);
        listener.startListening();

        verify(jmsProvider).getConnectionFactory();

        listener.stopListening();
    }
}
