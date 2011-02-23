package edu.gemini.aspen.gmp.commands.jms;

import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.impl.CommandUpdaterImpl;
import edu.gemini.aspen.gmp.commands.model.ActionManager;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class CompletionInfoListenerTest {
    @Test
    public void testOCSUpdate() throws JMSException {
        ActionManager actionManager = mock(ActionManager.class);
        CommandUpdater commandUpdater = new CommandUpdaterImpl(actionManager);
        CompletionInfoListener listener = new CompletionInfoListener(commandUpdater);
        MapMessage message = mock(MapMessage.class);
        when(message.getIntProperty(JmsKeys.GMP_ACTIONID_PROP)).thenReturn(1);
        when(message.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenReturn(HandlerResponse.Response.COMPLETED.toString());

        listener.onMessage(message);

        verify(actionManager).registerCompletionInformation(1, HandlerResponse.get(HandlerResponse.Response.COMPLETED));
    }
}
