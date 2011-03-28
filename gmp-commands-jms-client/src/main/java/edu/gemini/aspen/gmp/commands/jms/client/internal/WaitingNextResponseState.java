package edu.gemini.aspen.gmp.commands.jms.client.internal;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

/**
 * CommandSenderReply state after a STARTED response has been received but further responses are expected
 */
class WaitingNextResponseState extends CommandSenderState {
    WaitingNextResponseState(CommandSenderReply commandSenderReply) {
        super(commandSenderReply);
    }

    @Override
    public void setupCompletionListener(CompletionListener listener) {
        try {
            commandSenderReply.buildReplyConsumerOnCorrelationID().setMessageListener(new CommandCompletionMessageListener(commandSenderReply, listener));
        } catch (JMSException e) {
            listener.onHandlerResponse(HandlerResponse.createError(e.getMessage()), Command.noCommand());
        }
    }

    @Override
    public MessageConsumer buildReplyConsumer() throws JMSException {
        return commandSenderReply.buildReplyConsumerOnCorrelationID();
    }

    @Override
    public HandlerResponse sendCommandMessage(Command command, long timeout) {
        throw new IllegalStateException("Cannot send command message when the first reply has arrived");
    }

    @Override
    public void completionReceived() {
        commandSenderReply.stopJms();
    }
}
