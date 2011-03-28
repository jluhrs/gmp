package edu.gemini.aspen.gmp.commands.jms.client.internal;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

/**
 * An CommandSenderState represents the current state of a CommandSenderReply
 */
public abstract class CommandSenderState {
    protected final CommandSenderReply commandSenderReply;

    CommandSenderState(CommandSenderReply commandSenderReply) {
        this.commandSenderReply = commandSenderReply;
    }

    public abstract void setupCompletionListener(CompletionListener listener);

    public abstract MessageConsumer buildReplyConsumer() throws JMSException;

    public abstract HandlerResponse sendCommandMessage(Command command, long timeout);

    public void completionReceived() {}
}
