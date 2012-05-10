package edu.gemini.aspen.gmp.commands.jms.client;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.gmp.commands.jms.client.internal.CommandSenderReply;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.jms.api.MessagingException;

import javax.jms.JMSException;
import java.util.UUID;

/**
 * This class sends sequence commands to the GMP using the jms-client bridge
 * interface over JMS but it implements by itself the {@link edu.gemini.aspen.giapi.commands.CommandSender}
 * interface hiding whether the connection is local or remote
 * <br>
 * This class is not designed to be an OSGi service as it would conflict with other internal
 * CommandSender objects.
 * <br>
 * Instead it is meant to be used as a standalone client
 */
public class CommandSenderClient implements CommandSender {
    private final JmsProvider provider;

    public CommandSenderClient(JmsProvider provider) {
        Preconditions.checkArgument(provider != null, "Provider cannot be null");
        this.provider = provider;
    }

    @Override
    public HandlerResponse sendCommand(Command command, CompletionListener listener) {
        return connectAndSendCommand(command, listener, DEFAULT_COMMAND_RESPONSE_TIMEOUT);
    }

    @Override
    public HandlerResponse sendCommand(Command command, CompletionListener listener, long timeout) {
        return connectAndSendCommand(command, listener, timeout);
    }

    private HandlerResponse connectAndSendCommand(Command command, CompletionListener listener, long timeout) {
        try {
            return sendCommandAndWaitResponse(command, listener, timeout);
        } catch (MessagingException e) {
            // If there is an error capture it here and return it as a HandlerResponse
            return HandlerResponse.createError(e.getMessage());
        } catch (Exception e) {
            // If there is an error capture it here and return it as a HandlerResponse
            return HandlerResponse.createError(e.getMessage());
        }
    }

    private HandlerResponse sendCommandAndWaitResponse(Command command, CompletionListener listener, long timeout) {
        String correlationID = UUID.randomUUID().toString();
        CommandSenderReply commandSenderReply = new CommandSenderReply(correlationID);

        startConnection(commandSenderReply);

        HandlerResponse initialResponse = commandSenderReply.sendCommandMessage(command, timeout);

        commandSenderReply.setupCompletionListener(listener);

        return initialResponse;
    }

    private void startConnection(CommandSenderReply commandSenderReply) {
        try {
            commandSenderReply.startJms(provider);
        } catch (JMSException e) {
            throw new MessagingException("Exception while starting the JMS provider", e);
        }
    }

}
