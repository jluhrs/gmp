package edu.gemini.giapi.tool.commands;

import com.google.common.collect.Iterators;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapitestsupport.TesterException;
import edu.gemini.aspen.giapitestsupport.commands.CompletionListenerMock;
import edu.gemini.jms.api.MessagingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageListener;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandSenderClientTest extends MockedJMSArtifactsBase {
    private CommandSenderClient senderClient;
    private CompletionListenerMock completionListener;

    @Before
    public void setUp() throws JMSException, TesterException {
        createMockedObjects();
        senderClient = new CommandSenderClient(provider);
        completionListener = new CompletionListenerMock();
    }

    @Test
    public void testSendParkCommand() throws TesterException, JMSException {
        mockInitialResponseMessage("COMPLETED");

        Command command = new Command(SequenceCommand.PARK, Activity.START);
        HandlerResponse response = senderClient.sendCommand(command, completionListener);

        assertEquals(HandlerResponse.COMPLETED, response);
    }

    private void mockInitialResponseMessage(String responseType) throws JMSException {
        MapMessage message = mock(MapMessage.class);
        when(message.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenReturn(responseType);

        when(consumer.receive(anyInt())).thenReturn(message);
    }

    @Test
    public void testSendApplyWithLateResponse() throws TesterException, JMSException {
        mockInitialResponseMessage("STARTED");

        ArgumentCaptor<MessageListener> listenerCaptor = ArgumentCaptor.forClass(MessageListener.class);

        Command command = new Command(SequenceCommand.APPLY, Activity.START, emptyConfiguration());

        // Simulate that a reply was sent later on
        HandlerResponse response = senderClient.sendCommand(command, completionListener);
        assertEquals(HandlerResponse.STARTED, response);

        verify(consumer).setMessageListener(listenerCaptor.capture());
        MapMessage replyMessage = mockCompletionInformationMessage();

        listenerCaptor.getValue().onMessage(replyMessage);
        assertTrue(completionListener.wasInvoked());
    }

    private MapMessage mockCompletionInformationMessage() throws JMSException {
        MapMessage replyMessage = mock(MapMessage.class);
        when(replyMessage.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenReturn("COMPLETED");
        when(replyMessage.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY)).thenReturn("APPLY");
        when(replyMessage.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY)).thenReturn("START");
        when(replyMessage.getMapNames()).thenReturn(Iterators.asEnumeration(Iterators.<Object>emptyIterator()));
        return replyMessage;
    }

    @Test(expected = MessagingException.class)
    public void testSendCommandWhenDisconnected() throws TesterException, JMSException {
        Command command = new Command(SequenceCommand.PARK, Activity.START);
        senderClient.sendCommand(command, completionListener);
    }

}
