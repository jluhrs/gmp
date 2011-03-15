package edu.gemini.aspen.gmp.commands.jmsexecutors;

import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.impl.CommandUpdaterImpl;
import edu.gemini.aspen.gmp.commands.jms.CompletionInfoListener;
import edu.gemini.aspen.gmp.commands.model.IActionManager;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class CompletionInfoListenerTest {
    private IActionManager actionManager;
    private MapMessage message;

    @Before
    public void setUp() throws Exception {
        actionManager = mock(IActionManager.class);
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
