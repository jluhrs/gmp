package edu.gemini.aspen.gmp.commands.jms.client;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.DefaultConfiguration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.MessagingException;
import org.junit.Test;
import org.mockito.Matchers;

import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class CommandSenderReplyTest extends MockedJMSArtifactsBase {
    @Test
    public void testCommandSendWithImmediateResponse() throws JMSException {
        super.createMockedObjects();
        
        CommandSenderReply senderReply = new CommandSenderReply("1");
        senderReply.startJms(provider);

        // Mocking the response
        when(consumer.receive(Matchers.anyLong())).thenReturn(mapMessage);
        when(mapMessage.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY)).thenReturn("COMPLETED");

        Command command = new Command(SequenceCommand.APPLY, Activity.PRESET, DefaultConfiguration.emptyConfiguration());
        HandlerResponse response = senderReply.sendCommandMessage(command, 1000);

        assertEquals(HandlerResponse.Response.COMPLETED, response.getResponse());
    }

    @Test
    public void testCreateReplyConsumer() throws JMSException {
        super.createMockedObjects();

        CommandSenderReply senderReply = new CommandSenderReply("1");
        senderReply.startJms(provider);
        assertNotNull(senderReply.createReplyConsumer(mapMessage));
    }

    @Test(expected = MessagingException.class)
    public void testCreateReplyConsumerWhenNotConnected() throws JMSException {
        super.createMockedObjects();

        CommandSenderReply senderReply = new CommandSenderReply("1");
        assertNotNull(senderReply.createReplyConsumer(mapMessage));
    }

    @Test
    public void testCommandSendWithNoConnection() {
        CommandSenderReply senderReply = new CommandSenderReply("1");
        Command command = new Command(SequenceCommand.APPLY, Activity.PRESET, DefaultConfiguration.emptyConfiguration());
        HandlerResponse response = senderReply.sendCommandMessage(command, 1000);

        assertEquals(HandlerResponse.Response.ERROR, response.getResponse());
    }

}
