package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.JmsMapMessageSender;

import javax.jms.Destination;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class is an internal listener that gets notified when an Action is completed
 * and then forwards the completion message to a client to the destination in the
 * reply of the command request
 * <p/>
 * TODO This class has two responsibilities - separate them
 */
public class JmsForwardingCompletionListener extends JmsMapMessageSender implements CompletionListener {
    private static final Logger LOG = Logger.getLogger(JmsForwardingCompletionListener.class.getName());
    private final Destination _replyDestination;

    public JmsForwardingCompletionListener(Destination replyDestination) {
        super("");
        _replyDestination = replyDestination;
    }

    @Override
    public void onHandlerResponse(HandlerResponse response, Command command) {
        Map<String, String> message = convertConfigurationToProperties(command.getConfiguration());
        Map<String, String> properties = convertResponseAndCommandToProperties(response, command);

        // Send the reply message as a map message
        super.sendStringBasedMapMessage(_replyDestination, message, properties);
    }

    public void sendImmediateHandlerResponse(HandlerResponse response) {
        Map<String, String> message = CommandMessageSerializer.convertHandlerResponseToProperties(response);

        // Send the reply message as a map message
        super.sendStringBasedMapMessage(_replyDestination, message, ImmutableMap.<String, String>of());
    }

    /**
     * Translates a configuration into properties that will be included in the JMS Message body
     */
    protected Map<String, String> convertConfigurationToProperties(Configuration config) {
        Preconditions.checkArgument(config != null);
        Map<String, String> content = Maps.newHashMap();

        for (ConfigPath path : config.getKeys()) {
            content.put(path.getName(), config.getValue(path));
        }
        return content;
    }

    /**
     * Translates the response and properties
     *
     * @param response
     * @param command
     * @return
     */
    protected Map<String, String> convertResponseAndCommandToProperties(HandlerResponse response, Command command) {
        Map<String, String> properties = Maps.newHashMap();

        properties.putAll(CommandMessageSerializer.convertHandlerResponseToProperties(response));
        properties.putAll(convertCommandToProperties(command));

        return ImmutableMap.copyOf(properties);
    }

    private Map<String, String> convertCommandToProperties(Command command) {
        SequenceCommand sc = command.getSequenceCommand();
        Activity activity = command.getActivity();

        return ImmutableMap.of(
                JmsKeys.GMP_SEQUENCE_COMMAND_KEY, sc.name(),
                JmsKeys.GMP_ACTIVITY_KEY, activity.name());
    }

    @Deprecated
    public void onHandlerResponse(HandlerResponse response, SequenceCommand command, Activity activity, Configuration config) {
        onHandlerResponse(response, new Command(command, activity, config));
    }
}
