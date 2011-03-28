package edu.gemini.aspen.gmp.commands.jms.client.internal;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

/**
 * State after a COMPLETED handler response has arrived. At this state no further messages can be sent
 */
class CompletedState extends CommandSenderState {
    CompletedState(CommandSenderReply commandSenderReply) {
        super(commandSenderReply);
    }

    @Override
    public void setupCompletionListener(CompletionListener listener) {
        // We are already complete, just stop listening
        commandSenderReply.stopJms();
    }

    @Override
    public MessageConsumer buildReplyConsumer() throws JMSException {
        throw new IllegalStateException("Cannot create a new reply consumer of a completed command");
    }

    @Override
    public HandlerResponse sendCommandMessage(Command command, long timeout) {
        throw new IllegalStateException("Cannot send command message when the command has completed");
    }
}
