package edu.gemini.giapi.tool.commands;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;
import edu.gemini.aspen.giapitestsupport.TesterException;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsMapMessageSenderReply;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class sends sequence commands to the GMP using the bridge
 * interface over JMS but it implements by itself the {@link edu.gemini.aspen.giapi.commands.CommandSender}
 * interface hiding whether the connection is local or remote
 * <p/>
 * This class is not designed to be an OSGi service as it would conflict with other internal
 * CommandSender objects.
 * <p/>
 * Instead it is meant to be used as a standalone client
 */
public class CommandSenderClient extends JmsMapMessageSenderReply<HandlerResponse> implements CommandSender {
    private static final Logger LOG = Logger.getLogger(CommandSenderClient.class.getName());

    private JmsProvider _provider;

    public CommandSenderClient(JmsProvider provider) throws TesterException {
        super("");
        Preconditions.checkArgument(provider != null, "Provider cannot be null");
        this._provider = provider;

        try {
            startJms(provider);
        } catch (JMSException e) {
            LOG.log(Level.SEVERE, "Exception when connecting to the JMS broker", e);
        }
    }

    @Override
    public HandlerResponse sendCommand(Command command, CompletionListener listener) {
        return sendCommandIfPossible(command, 1000);
    }

    private HandlerResponse sendCommandIfPossible(Command command, long timeout) {
        if (isConnected()) {
            return sendCommandAndWaitForReply(command, timeout);
        } else {
            return HandlerResponse.createError("Not connected");
        }
    }

    private HandlerResponse sendCommandAndWaitForReply(Command command, long timeout) {
        DestinationData destination = new DestinationData(JmsKeys.GW_COMMAND_TOPIC, DestinationType.TOPIC);
        Map<String, String> message = ImmutableMap.of();
        Map<String, String> properties = ImmutableMap.of(
                JmsKeys.GMP_SEQUENCE_COMMAND_KEY, command.getSequenceCommand().name(),
                JmsKeys.GMP_ACTIVITY_KEY, command.getActivity().name()
        );

        HandlerResponse handlerResponse = super.sendStringBasedMapMessageReply(destination, message, properties, timeout);
        return handlerResponse;
    }

    @Override
    public HandlerResponse sendCommand(Command command, CompletionListener listener, long timeout) {
        return sendCommandIfPossible(command, timeout);
    }

    @Override
    public HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity, CompletionListener listener) {
        throw new UnsupportedOperationException("Use sendCommand instead");
    }

    @Override
    public HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity, Configuration config, CompletionListener listener) {
        throw new UnsupportedOperationException("Use sendCommand instead");
    }

    @Override
    protected HandlerResponse buildResponse(Message reply) throws JMSException {
        return MessageBuilder.buildHandlerResponse(reply);
    }

}
