package edu.gemini.aspen.gmp.commands.jms.client.internal;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.HandlerResponseMessageParser;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.DestinationBuilder;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsMapMessageSenderReply;
import edu.gemini.jms.api.MessagingException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

/**
 * This class is able to send a command message to the GW_COMMAND_TOPIC
 * and receive the immediate response of the command
 * <br>
 * Additionally it can keep listening for commands in case it is necessary
 * This happens when the command doesn't complete immediately
 */
public class CommandSenderReply extends JmsMapMessageSenderReply<HandlerResponse> {
    private static final DestinationData REQUESTS_REPLY_DESTINATION = new DestinationData(JmsKeys.GW_COMMAND_REPLY_QUEUE, DestinationType.QUEUE);
    private final static DestinationBuilder DESTINATION_BUILDER = new DestinationBuilder();
    private final String correlationID;

    // This object has a state that changes upon receiving responses
    private CommandSenderState state;

    public CommandSenderReply(String correlationID) {
        super("Command Reply Listener on correlationID: " + correlationID);
        Preconditions.checkArgument(correlationID != null, "Correlation ID cannot be null");
        Preconditions.checkArgument(!correlationID.isEmpty(), "Correlation ID cannot be empty");
        
        this.correlationID = correlationID;
        state = new InitialState(this);
    }

    public HandlerResponse sendCommandMessage(Command command, long timeout) {
        if (isConnected()) {
            HandlerResponse initialResponse = state.sendCommandMessage(command, timeout);
            switchState(initialResponse);

            return initialResponse;
        } else {
            return HandlerResponse.createError("Not connected");
        }
    }

    private void switchState(HandlerResponse initialResponse) {
        if (initialResponse == HandlerResponse.STARTED) {
            state = new WaitingNextResponseState(this);
        } else {
            state = new CompletedState(this);
            completionReceived();
        }
    }

    public void setupCompletionListener(CompletionListener listener) {
         state.setupCompletionListener(listener);
    }

    @Override
    protected MessageConsumer createReplyConsumer(Message requestMessage) throws JMSException {
        if (isConnected()) {
            return state.buildReplyConsumer();
        } else {
            throw new MessagingException("Cannot build a reply destination if not connected");
        }
    }

    MessageConsumer buildReplyConsumerOnCorrelationID() throws MessagingException {
        try {
            Destination replyDestination = DESTINATION_BUILDER.newDestination(REQUESTS_REPLY_DESTINATION, _session);
            return _session.createConsumer(replyDestination, "JMSCorrelationID=\'" + correlationID + "\'");
        } catch (JMSException e) {
            throw new MessagingException("Exception when building the reply consumer", e);
        }
    }

    @Override
    protected HandlerResponse buildResponse(Message reply) throws MessagingException {
        HandlerResponseMessageParser parser = new HandlerResponseMessageParser(reply);
        return parser.readResponse();
    }

    String getCorrelationID() {
        return correlationID;
    }

    void completionReceived() {
        state.completionReceived();
    }
}