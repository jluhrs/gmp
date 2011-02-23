package edu.gemini.aspen.gmp.commands.jms;

import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.impl.CommandUpdaterImpl;
import edu.gemini.aspen.gmp.commands.model.ActionManager;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import static org.mockito.Mockito.*;

public class CompletionInfoListenerTest {
    private ActionManager actionManager;
    private MapMessage message;

    @Before
    public void setUp() throws Exception {
        actionManager = mock(ActionManager.class);
        message = mock(MapMessage.class);
    }

    @Test
    public void testOCSUpdate() throws JMSException {
        CommandUpdater commandUpdater = new CommandUpdaterImpl(actionManager);
        CompletionInfoListener listener = new CompletionInfoListener(commandUpdater);
        when(message.getIntProperty(JmsKeys.GMP_ACTIONID_PROP)).thenReturn(1);
        when(message.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenReturn(HandlerResponse.Response.COMPLETED.toString());

        listener.onMessage(message);

        verify(actionManager).registerCompletionInformation(1, HandlerResponse.get(HandlerResponse.Response.COMPLETED));
    }

    @Test(expected = SequenceCommandException.class)
    public void testExceptionOnMessageQuerying() throws JMSException {
        CommandUpdater commandUpdater = new CommandUpdaterImpl(actionManager);
        CompletionInfoListener listener = new CompletionInfoListener(commandUpdater);
        when(message.getIntProperty(JmsKeys.GMP_ACTIONID_PROP)).thenThrow(new JMSException("exception"));

        listener.onMessage(message);
    }

    @Test
    public void testNoMapMessage() throws JMSException {
        CommandUpdater commandUpdater = new CommandUpdaterImpl(actionManager);
        CompletionInfoListener listener = new CompletionInfoListener(commandUpdater);
        Message message = mock(Message.class);

        listener.onMessage(message);

        verifyZeroInteractions(actionManager);
    }
}
