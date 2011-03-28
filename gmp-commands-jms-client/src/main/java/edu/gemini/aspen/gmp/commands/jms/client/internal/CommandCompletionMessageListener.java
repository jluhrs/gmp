package edu.gemini.aspen.gmp.commands.jms.client.internal;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionInformation;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * These objects will get JMS Messages with Completion Information and will forward them
 * to the CompletionListener after decoding.
 */
class CommandCompletionMessageListener implements MessageListener {
    private final CompletionListener listener;
    private CommandSenderReply commandSenderReply;

    public CommandCompletionMessageListener(CommandSenderReply commandSenderReply, CompletionListener listener) {
        Preconditions.checkArgument(commandSenderReply != null);
        Preconditions.checkArgument(listener != null);

        this.listener = listener;
        this.commandSenderReply = commandSenderReply;
    }

    @Override
    public void onMessage(Message message) {
        CompletionInformation completionInformation = null;
        try {
            // Decode message and pass it forward
            completionInformation = MessageBuilder.buildCompletionInformation(message);
            listener.onHandlerResponse(completionInformation.getHandlerResponse(), completionInformation.getCommand());
        } catch (Exception e) {
            reportExceptionToListener(completionInformation, e);
        } finally {
            commandSenderReply.completionReceived();
        }
    }

    private void reportExceptionToListener(CompletionInformation completionInformation, Exception e) {
        if (completionInformation != null) {
            listener.onHandlerResponse(completionInformation.getHandlerResponse(), completionInformation.getCommand());
        } else {
            listener.onHandlerResponse(HandlerResponse.createError(e.getMessage()), Command.noCommand());
        }
    }
}
