package edu.gemini.aspen.gmp.commands.jms.client.internal;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.test.MapMessageMock;
import edu.gemini.aspen.giapitestsupport.commands.CompletionListenerMock;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandCompletionListenerTest {
    private CompletionListenerMock listener;
    private CommandSenderReply commandSenderReply;
    private CommandCompletionMessageListener messageListener;

    @Before
    public void setUp() throws Exception {
        listener = new CompletionListenerMock();
        commandSenderReply = new CommandSenderReply("1");
        messageListener = new CommandCompletionMessageListener(commandSenderReply, listener);
    }

    @Test
    public void testNormalCompletion() throws JMSException {
        MapMessage message = new MapMessageMock();
        message.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "COMPLETED");
        message.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, "PARK");
        message.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, "START");

        messageListener.onMessage(message);

        assertTrue(listener.wasInvoked());
    }

    @Test
    public void testAbnormalCompletion() throws JMSException {
        MapMessage message = mock(MapMessage.class);
        when(message.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenThrow(new JMSException(""));

        messageListener.onMessage(message);

        assertTrue(listener.wasInvoked());
    }

    @Test
    public void testBadlyFormedCompletion() throws JMSException {
        MapMessage message = new MapMessageMock();
        message.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "UNKNOWN");

        messageListener.onMessage(message);

        assertTrue(listener.wasInvoked());
    }

}
