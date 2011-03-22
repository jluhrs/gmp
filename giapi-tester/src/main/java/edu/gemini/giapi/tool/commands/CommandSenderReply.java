package edu.gemini.giapi.tool.commands;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionInformation;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;
import edu.gemini.aspen.gmp.commands.jms.clientbridge.CommandReplyMapMessageBuilder;
import edu.gemini.jms.api.DestinationBuilder;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsMapMessageSenderReply;
import edu.gemini.jms.api.MapMessageBuilder;
import edu.gemini.jms.api.MessagingException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import java.util.Map;

/**
 * This class is able to send a command message to the GW_COMMAND_TOPIC
 * and receive the immediate response of the command
 * <p/>
 * Additionally it can keep listening for commands in case it is necessary
 * This happens when the command doesn't complete immediately
 */
public class CommandSenderReply extends JmsMapMessageSenderReply<HandlerResponse> {
    private static final DestinationData REQUESTS_DESTINATION = new DestinationData(JmsKeys.GW_COMMAND_TOPIC, DestinationType.TOPIC);
    private static final DestinationData REQUESTS_REPLY_DESTINATION = new DestinationData(JmsKeys.GW_COMMAND_REPLY_QUEUE, DestinationType.QUEUE);
    private final String correlationID;
    private final static DestinationBuilder destinationBuilder = new DestinationBuilder();
    private MessageConsumer replyConsumer;


    private enum CommandSenderState {
        INIT, INITIAL_RESPONSE_READY, COMMAND_COMPLETE;
    }

    private CommandSenderState state = CommandSenderReply.CommandSenderState.INIT;

    public CommandSenderReply(String correlationID) {
        super("Command Reply Listener on correlationID: " + correlationID);
        Preconditions.checkArgument(correlationID != null, "Correlation ID cannot be null");
        Preconditions.checkArgument(!correlationID.isEmpty(), "Correlation ID cannot be empty");
        this.correlationID = correlationID;
    }

    public HandlerResponse sendCommandMessage(Command command, long timeout) {
        if (isConnected() && state == CommandSenderState.INIT) {
            Map<String, String> message = ImmutableMap.of();
            Map<String, String> properties = ImmutableMap.of(
                    JmsKeys.GMP_SEQUENCE_COMMAND_KEY, command.getSequenceCommand().name(),
                    JmsKeys.GMP_ACTIVITY_KEY, command.getActivity().name()
            );
            MapMessageBuilder messageBuilder = new CommandReplyMapMessageBuilder(correlationID, message, properties);
            HandlerResponse initialResponse = sendMessageWithReply(REQUESTS_DESTINATION, messageBuilder, timeout);
            if (initialResponse == HandlerResponse.COMPLETED) {
                state = CommandSenderReply.CommandSenderState.COMMAND_COMPLETE;
            } else {
                state = CommandSenderReply.CommandSenderState.INITIAL_RESPONSE_READY;
            }
            return initialResponse;
        } else {
            return HandlerResponse.createError("Not connected");
        }
    }

    public void setupCompletionListener(CompletionListener listener) {
        if (state == CommandSenderReply.CommandSenderState.INIT || state == CommandSenderReply.CommandSenderState.COMMAND_COMPLETE) {
            stopJms();
        } else {
            try {
                buildReplyConsumerOnCorrelationID().setMessageListener(new CompletionMessageListener(listener));
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected MessageConsumer createReplyConsumer(Message requestMessage) throws JMSException {
        if (isConnected()) {
            if (state == CommandSenderReply.CommandSenderState.INIT) {
                replyConsumer = buildReplyConsumerOnCorrelationID();
                return replyConsumer;
            } else if (state == CommandSenderReply.CommandSenderState.INITIAL_RESPONSE_READY) {
                replyConsumer = buildReplyConsumerOnCorrelationID();
                return replyConsumer;
            }
            throw new MessagingException("Cannot build a reply destination in completed state");
        } else {
            throw new MessagingException("Cannot build a reply destination if not connected");
        }
    }

    private MessageConsumer buildReplyConsumerOnCorrelationID() throws JMSException {
        Destination replyDestination = destinationBuilder.newDestination(REQUESTS_REPLY_DESTINATION, _session);
        return _session.createConsumer(replyDestination, "JMSCorrelationID=\'" + correlationID + "\'");
    }

    @Override
    protected HandlerResponse buildResponse(Message reply) throws JMSException {
        return MessageBuilder.buildHandlerResponse(reply);
    }

    private class CompletionMessageListener implements MessageListener {
        private final CompletionListener listener;

        public CompletionMessageListener(CompletionListener listener) {
            this.listener = listener;
        }

        @Override
        public void onMessage(Message message) {
            CompletionInformation completionInformation = null;
            try {
                completionInformation = MessageBuilder.buildCompletionInformation(message);
                listener.onHandlerResponse(completionInformation.getHandlerResponse(), completionInformation.getCommand());
            } catch (JMSException e) {
                e.printStackTrace();
            } finally {
                CommandSenderReply.this.stopJms();
            }
        }
    }
}