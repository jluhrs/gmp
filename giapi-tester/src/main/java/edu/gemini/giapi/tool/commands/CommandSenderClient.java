package edu.gemini.giapi.tool.commands;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapitestsupport.TesterException;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.UUID;
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
public class CommandSenderClient implements CommandSender {
    private static final Logger LOG = Logger.getLogger(CommandSenderClient.class.getName());
    private final JmsProvider provider;

    public CommandSenderClient(JmsProvider provider) throws TesterException {
        this.provider = provider;
        Preconditions.checkArgument(provider != null, "Provider cannot be null");
    }

    @Override
    public HandlerResponse sendCommand(Command command, CompletionListener listener) {
        return sendCommandIfPossible(command, 5000, listener);
    }

    private HandlerResponse sendCommandIfPossible(Command command, long timeout, CompletionListener listener) {
        String correlationID = UUID.randomUUID().toString();
        CommandSenderReply commandSenderReply = new CommandSenderReply(correlationID);
        try {
            commandSenderReply.startJms(provider);
        } catch (JMSException e) {
            return HandlerResponse.createError("Not connected");
        }

        HandlerResponse initialResponse = commandSenderReply.sendCommandMessage(command, timeout);

        if (!initialResponse.equals(HandlerResponse.Response.COMPLETED)) {
            commandSenderReply.waitForCompletionResponse(listener);
            //System.out.println("Completion Information: " + completionResponse);
            /*DestinationData replyDestination = new DestinationData(JmsKeys.GW_COMMAND_REPLY_TOPIC, DestinationType.TOPIC);
            DestinationBuilder builder = new DestinationBuilder();
            try {
                System.out.println(this.waitForReplyMessage(builder.newDestination(replyDestination, this._session), timeout));
            } catch (JMSException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }*/
            //HandlerResponse lateResponse = this.waitForReply(, timeout);
        } else {
            commandSenderReply.stopJms();
        }
        return initialResponse;
    }

    @Override
    public HandlerResponse sendCommand(Command command, CompletionListener listener, long timeout) {
        return sendCommandIfPossible(command, timeout, listener);
    }

    @Override
    public HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity, CompletionListener listener) {
        throw new UnsupportedOperationException("Use sendCommand instead");
    }

    @Override
    public HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity, Configuration config, CompletionListener listener) {
        throw new UnsupportedOperationException("Use sendCommand instead");
    }

}
