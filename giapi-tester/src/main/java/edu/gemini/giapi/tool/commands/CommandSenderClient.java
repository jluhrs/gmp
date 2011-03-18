package edu.gemini.giapi.tool.commands;

import com.google.common.collect.ImmutableMap;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;
import edu.gemini.aspen.giapitestsupport.TesterException;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsMapMessageSenderReply;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Map;

/**
 * This class sends sequence commands to the GMP using the bridge
 * interface over JMS but it implements by itself the {@link edu.gemini.aspen.giapi.commands.CommandSender}
 * interface hiding whether the connection is local or remote
 */
public class CommandSenderClient extends JmsMapMessageSenderReply<HandlerResponse> implements CommandSender {

    private JmsProvider _provider;

    public CommandSenderClient(JmsProvider provider) throws TesterException {
        super("");
        this._provider = provider;

        try {
            startJms(provider);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HandlerResponse sendCommand(Command command, CompletionListener listener) {
        DestinationData destination = new DestinationData(JmsKeys.GW_COMMAND_TOPIC, DestinationType.TOPIC);
        Map<String, String> message = ImmutableMap.of();
        Map<String, String> properties = ImmutableMap.of(
                JmsKeys.GMP_SEQUENCE_COMMAND_KEY, command.getSequenceCommand().name(),
                JmsKeys.GMP_ACTIVITY_KEY, command.getActivity().name()
        );

        HandlerResponse handlerResponse = super.sendStringBasedMapMessageReply(destination, message, properties, 1000);
        return handlerResponse;
    }


    @Override
    public HandlerResponse sendCommand(Command command, CompletionListener listener, long timeout) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity, CompletionListener listener) {
        return sendCommand(new Command(command, activity), listener);
    }

    @Override
    public HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity, Configuration config, CompletionListener listener) {
        return sendCommand(new Command(command, activity, config), listener);
    }

    @Override
    protected HandlerResponse buildResponse(Message reply) throws JMSException {
        return parseHandlerResponse(reply);
    }

    private HandlerResponse parseHandlerResponse(Message reply) throws JMSException {
        return MessageBuilder.buildHandlerResponse(reply);
    }
}
