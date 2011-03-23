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
import edu.gemini.aspen.giapi.util.jms.GMPCommandMessageBuilder;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.JmsMapMessageSender;
import edu.gemini.jms.api.MapMessageBuilder;

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
    private final DestinationData _replyDestination;
    private final String _correlationID;

    public JmsForwardingCompletionListener(DestinationData replyDestination, String correlationID) {
        super("");
        Preconditions.checkArgument(replyDestination != null, "Destination to send responses cannot be null");
        Preconditions.checkArgument(correlationID != null && !correlationID.isEmpty(), "All Command messages require a _correlationID");

        _replyDestination = replyDestination;
        this._correlationID = correlationID;
    }

    static Map<String, String> convertHandlerResponseToProperties(HandlerResponse response) {
        Map<String, String> properties = Maps.newHashMap();

        properties.put(JmsKeys.GMP_HANDLER_RESPONSE_KEY, response.getResponse().name());
        if (response.hasErrorMessage()) {
            properties.put(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
        }
        return properties;
    }

    @Override
    public void onHandlerResponse(HandlerResponse response, Command command) {
        LOG.fine("Arrived response " + response + " forward to " + _replyDestination);
        sendCompletionResponse(response, command);

        stopListeningForResponses();
    }

    void stopListeningForResponses() {
        this.stopJms();
    }

    /**
     * Sends to the client that sent the command the initial response to the request
     *
     * @param response The initial response returned by CommandSender.sendCommand
     */
    void sendInitialResponse(HandlerResponse response) {
        LOG.fine("Sent initial response " + response + " to " + _replyDestination + " " + _correlationID);
        Map<String, String> messageBody = convertHandlerResponseToProperties(response);

        MapMessageBuilder messageBuilder = new GMPCommandMessageBuilder(_correlationID, messageBody, ImmutableMap.<String, String>of());
        super.sendMapMessage(_replyDestination, messageBuilder);
    }

    private void sendCompletionResponse(HandlerResponse response, Command command) {
        Map<String, String> message = convertConfigurationToProperties(command.getConfiguration());
        Map<String, String> properties = convertResponseAndCommandToProperties(response, command);

        MapMessageBuilder messageBuilder = new GMPCommandMessageBuilder(_correlationID, message, properties);
        super.sendMapMessage(_replyDestination, messageBuilder);
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

    @Override
    public String toString() {
        return "ForwardingCompletionListener on " + _replyDestination.getName();
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

        properties.putAll(convertHandlerResponseToProperties(response));
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

}
