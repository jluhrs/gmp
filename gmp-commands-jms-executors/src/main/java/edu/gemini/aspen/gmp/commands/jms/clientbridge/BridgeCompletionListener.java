package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import com.google.common.base.Preconditions;
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
 */
public class BridgeCompletionListener extends JmsMapMessageSender implements CompletionListener {
    private static final Logger LOG = Logger.getLogger(BridgeCompletionListener.class.getName());
    private final Destination _destination;

    public BridgeCompletionListener(Destination destination) {
        super("");
        _destination = destination;
    }

    @Override
    public void onHandlerResponse(HandlerResponse response, Command command) {
        Map<String, String> message = buildMessageContent(command.getConfiguration());
        Map<String, String> properties = buildProperties(response, command);

        // Send the reply message
        super.sendStringBasedMapMessage(_destination, message, properties);
    }

    protected Map<String, String> buildMessageContent(Configuration config) {
        Preconditions.checkArgument(config != null);
        Map<String, String> content = Maps.newHashMap();

        for (ConfigPath path : config.getKeys()) {
            content.put(path.getName(), config.getValue(path));
        }
        return content;
    }

    protected Map<String, String> buildProperties(HandlerResponse response, Command command) {
        Map<String, String> properties = Maps.newHashMap();
        properties.put(JmsKeys.GMP_HANDLER_RESPONSE_KEY, response.getResponse().name());
        if (response.hasErrorMessage()) {
            properties.put(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
        }

        SequenceCommand sc = command.getSequenceCommand();
        properties.put(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, sc.name());

        Activity activity = command.getActivity();
        properties.put(JmsKeys.GMP_ACTIVITY_KEY, activity.name());

        return properties;
    }

    @Deprecated
    public void onHandlerResponse(HandlerResponse response, SequenceCommand command, Activity activity, Configuration config) {
        onHandlerResponse(response, new Command(command, activity, config));
    }
}
