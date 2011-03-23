package edu.gemini.aspen.gmp.commands.jms.client.internal;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.jms.client.HandlerResponseMapMessage;
import edu.gemini.aspen.gmp.commands.jms.client.MockedJMSArtifactsBase;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class InitCommandSenderTest extends MockedJMSArtifactsBase {
    private String correlationID = UUID.randomUUID().toString();

    @Test
    public void testSendStartCommand() throws JMSException {
        super.createMockedObjects();

        CommandSenderReply commandSenderReply = new CommandSenderReply(correlationID);
        commandSenderReply.startJms(provider);
        
        InitCommandSenderState senderState = new InitCommandSenderState(commandSenderReply);
        Command command = new Command(SequenceCommand.INIT, Activity.START);

        mockReplyMessage(new HandlerResponseMapMessage(HandlerResponse.get(HandlerResponse.Response.COMPLETED)));
        
        senderState.sendCommandMessage(command, 1000);

        // Verify that the message was written correctly
        assertEquals(SequenceCommand.INIT.toString(), mapMessage.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY));
        assertEquals(Activity.START.toString(), mapMessage.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY));
    }

}
