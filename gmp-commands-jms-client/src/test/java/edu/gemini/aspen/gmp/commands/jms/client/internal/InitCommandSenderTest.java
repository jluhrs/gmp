package edu.gemini.aspen.gmp.commands.jms.client.internal;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.jms.client.HandlerResponseMapMessage;
import edu.gemini.aspen.gmp.commands.jms.client.MockedJMSArtifactsBase;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.UUID;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;
import static org.junit.Assert.assertEquals;

public class InitCommandSenderTest extends MockedJMSArtifactsBase {
    private String correlationID = UUID.randomUUID().toString();

    @Test
    public void testSendStartCommand() throws JMSException {
        super.createMockedObjects();

        CommandSenderReply commandSenderReply = new CommandSenderReply(correlationID);
        commandSenderReply.startJms(provider);

        InitialState senderState = new InitialState(commandSenderReply);
        Command command = new Command(SequenceCommand.INIT, Activity.START);

        mockReplyMessage(new HandlerResponseMapMessage(HandlerResponse.get(HandlerResponse.Response.COMPLETED)));

        senderState.sendCommandMessage(command, 1000);

        // Verify that the message was written correctly
        assertEquals(SequenceCommand.INIT.toString(), mapMessage.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY));
        assertEquals(Activity.START.toString(), mapMessage.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY));
    }

    @Test
    public void testSendStartCommandWithApply() throws JMSException {
        super.createMockedObjects();

        CommandSenderReply commandSenderReply = new CommandSenderReply(correlationID);
        commandSenderReply.startJms(provider);

        InitialState senderState = new InitialState(commandSenderReply);
        Configuration configuration = configurationBuilder()
                .withPath(configPath("x:A.v"), "1")
                .withPath(configPath("x:B.v"), "2")
                .build();

        Command command = new Command(SequenceCommand.APPLY, Activity.START, configuration);

        mockReplyMessage(new HandlerResponseMapMessage(HandlerResponse.get(HandlerResponse.Response.COMPLETED)));

        senderState.sendCommandMessage(command, 1000);

        // Verify that the message was written correctly
        assertEquals(SequenceCommand.APPLY.toString(), mapMessage.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY));
        assertEquals(Activity.START.toString(), mapMessage.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY));

        // Verify the config is being sent
        assertEquals("1", mapMessage.getString("x:A.v"));
        assertEquals("2", mapMessage.getString("x:B.v"));
    }

}
