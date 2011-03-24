package edu.gemini.aspen.gmp.commands.jms.client.internal;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.messagebuilders.StringBasedWithCorrelationIDMessageBuilder;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.MapMessageBuilder;
import edu.gemini.jms.api.MessagingException;

import javax.jms.MessageConsumer;
import java.util.Map;
import java.util.Set;

class InitCommandSenderState extends CommandSenderState {
    private static final DestinationData REQUESTS_DESTINATION = new DestinationData(JmsKeys.GW_COMMAND_TOPIC, DestinationType.TOPIC);

    InitCommandSenderState(CommandSenderReply commandSenderReply) {
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
        Map<String, String> message = Maps.newHashMap();
        Set<ConfigPath> paths = command.getConfiguration().getKeys();
        for (ConfigPath path : paths) {
            message.put(path.getName(), command.getConfiguration().getValue(path));
        }
        Map<String, String> properties = ImmutableMap.of(
                JmsKeys.GMP_SEQUENCE_COMMAND_KEY, command.getSequenceCommand().name(),
                JmsKeys.GMP_ACTIVITY_KEY, command.getActivity().name()
        );
        MapMessageBuilder messageBuilder = new StringBasedWithCorrelationIDMessageBuilder(commandSenderReply.getCorrelationID(), message, properties);
        return commandSenderReply.sendMessageWithReply(REQUESTS_DESTINATION, messageBuilder, timeout);
    }
}
