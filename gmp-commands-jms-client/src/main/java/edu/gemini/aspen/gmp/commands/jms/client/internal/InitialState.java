package edu.gemini.aspen.gmp.commands.jms.client.internal;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.MessageBuilderFactory;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.MapMessageBuilder;
import edu.gemini.jms.api.MessagingException;

import javax.jms.MessageConsumer;

/**
 * The initial state of a CommandSender before sending a request
 */
class InitialState extends CommandSenderState {
    private static final DestinationData REQUESTS_DESTINATION = new DestinationData(JmsKeys.GW_COMMAND_TOPIC, DestinationType.TOPIC);

    InitialState(CommandSenderReply commandSenderReply) {
        super(commandSenderReply);
    }

    @Override
    public void setupCompletionListener(CompletionListener listener) {
        // if in init state we call for a completion listener just stop
        throw new IllegalStateException("Cannot listen for completion messages if not started");
    }

    @Override
    public MessageConsumer buildReplyConsumer() throws MessagingException {
        return commandSenderReply.buildReplyConsumerOnCorrelationID();
    }

    @Override
    public HandlerResponse sendCommandMessage(Command command, long timeout) {
        MapMessageBuilder messageBuilder = MessageBuilderFactory.newMessageBuilder(command, commandSenderReply.getCorrelationID());

        return commandSenderReply.sendMessageWithReply(REQUESTS_DESTINATION, messageBuilder, timeout);
    }
}
