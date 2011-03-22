package edu.gemini.giapi.tool.commands;

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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import java.util.Map;

public class CommandSenderReply extends JmsMapMessageSenderReply<HandlerResponse> {
    static final DestinationData REQUESTS_DESTINATION = new DestinationData(JmsKeys.GW_COMMAND_TOPIC, DestinationType.TOPIC);
    private static final DestinationData REQUESTS_REPLY_DESTINATION = new DestinationData(JmsKeys.GW_COMMAND_REPLY_TOPIC, DestinationType.QUEUE);
    private final String correlationID;

    public CommandSenderReply(String correlationID) {
        super("Command Reply Listener on correlationID: " + correlationID);
        this.correlationID = correlationID;
    }

    @Override
    protected MessageConsumer createReplyConsumer(Message requestMessage) throws JMSException {
        Destination replyDestination = new DestinationBuilder().newDestination(REQUESTS_REPLY_DESTINATION, _session);
        return _session.createConsumer(replyDestination, "JMSCorrelationID=\'" + correlationID + "\'");
    }

    @Override
    protected HandlerResponse buildResponse(Message reply) throws JMSException {
        return MessageBuilder.buildHandlerResponse(reply);
    }

    public HandlerResponse sendCommandMessage(Command command, long timeout) {
        Map<String, String> message = ImmutableMap.of();
        Map<String, String> properties = ImmutableMap.of(
                JmsKeys.GMP_SEQUENCE_COMMAND_KEY, command.getSequenceCommand().name(),
                JmsKeys.GMP_ACTIVITY_KEY, command.getActivity().name()
        );
        MapMessageBuilder messageBuilder = new CommandReplyMapMessageBuilder(correlationID, message, properties);
        return sendMessageWithReply(REQUESTS_DESTINATION, messageBuilder, timeout);
    }

    public void waitForCompletionResponse(final CompletionListener timeout) {
        try {
            MessageConsumer consumer = createReplyConsumer(null);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    CompletionInformation completionInformation = null;
                    try {
                        completionInformation = MessageBuilder.buildCompletionInformation(message);
                    } catch (JMSException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    timeout.onHandlerResponse(completionInformation.getHandlerResponse(), completionInformation.getCommand());
                }
            });
        } catch (JMSException e) {
//            return HandlerResponse.createError(e.getMessage());
            e.printStackTrace();
        }
    }
}