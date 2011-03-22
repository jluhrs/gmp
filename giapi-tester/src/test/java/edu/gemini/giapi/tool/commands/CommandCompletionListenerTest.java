package edu.gemini.giapi.tool.commands;

import com.google.common.collect.Iterators;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapitestsupport.commands.CompletionListenerMock;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandCompletionListenerTest {
    @Test
    public void testNormalCompletion() throws JMSException {
        CompletionListenerMock listener = new CompletionListenerMock();
        CommandSenderReply commandSenderReply = new CommandSenderReply("1");
        CommandCompletionMessageListener messageListener = new CommandCompletionMessageListener(commandSenderReply, listener);
        MapMessage message= mock(MapMessage.class);
        when(message.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenReturn("COMPLETED");
        when(message.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY)).thenReturn("PARK");
        when(message.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY)).thenReturn("START");
        when(message.getMapNames()).thenReturn(Iterators.asEnumeration(Iterators.<Object>emptyIterator()));

        messageListener.onMessage(message);

        assertTrue(listener.wasInvoked());
    }

    @Test
    public void testAbnormalCompletion() throws JMSException {
        CompletionListenerMock listener = new CompletionListenerMock();
        CommandSenderReply commandSenderReply = new CommandSenderReply("1");
        CommandCompletionMessageListener messageListener = new CommandCompletionMessageListener(commandSenderReply, listener);
        MapMessage message= mock(MapMessage.class);
        when(message.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenThrow(new JMSException(""));

        messageListener.onMessage(message);

        assertFalse(listener.wasInvoked());
    }
}
