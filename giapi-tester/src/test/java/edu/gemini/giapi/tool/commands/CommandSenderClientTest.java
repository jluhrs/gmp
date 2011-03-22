package edu.gemini.giapi.tool.commands;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapitestsupport.TesterException;
import org.junit.Ignore;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandSenderClientTest extends MockedJMSArtifactsBase {

    @Test
    public void testSendParkCommand() throws TesterException, JMSException {
        createMockedObjects();

        MapMessage message = mock(MapMessage.class);
        when(message.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenReturn("COMPLETED");

        when(consumer.receive(anyInt())).thenReturn(message);
        CommandSenderClient senderClient = new CommandSenderClient(provider);

        Command command = new Command(SequenceCommand.PARK, Activity.START);
        CompletionListener completionListener = mock(CompletionListener.class);

        HandlerResponse response = senderClient.sendCommand(command, completionListener);

        assertEquals(HandlerResponse.COMPLETED, response);
    }

    @Test
    @Ignore
    public void testSendCommandWhenDisconnected() throws TesterException, JMSException {
        createMockedObjects();
        CommandSenderClient senderClient = new CommandSenderClient(provider);
        //senderClient.stopJms();

        Command command = new Command(SequenceCommand.PARK, Activity.START);
        CompletionListener completionListener = mock(CompletionListener.class);
        HandlerResponse response = senderClient.sendCommand(command, completionListener);

        assertEquals(HandlerResponse.createError("Not connected"), response);
    }

}
