package edu.gemini.giapi.tool.commands;

import edu.gemini.aspen.giapi.commands.CompletionInformation;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

class CommandCompletionMessageListener implements MessageListener {
    private final CompletionListener listener;
    private CommandSenderReply commandSenderReply;

    public CommandCompletionMessageListener(CommandSenderReply commandSenderReply, CompletionListener listener) {
        this.listener = listener;
        this.commandSenderReply = commandSenderReply;
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
            commandSenderReply.completionReceived();
        }
    }
}
