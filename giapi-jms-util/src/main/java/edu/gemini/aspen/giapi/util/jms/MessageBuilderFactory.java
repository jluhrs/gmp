package edu.gemini.aspen.giapi.util.jms;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionInformation;
import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.messagebuilders.StringBasedWithCorrelationIDMessageBuilder;
import edu.gemini.jms.api.MapMessageBuilder;

import java.util.Map;
import java.util.Set;

public class MessageBuilderFactory {
    public static MapMessageBuilder newMessageBuilder(HandlerResponse response, String correlationID) {
        Map<String, String> messageBody = convertHandlerResponseToProperties(response);

        MapMessageBuilder messageBuilder = new StringBasedWithCorrelationIDMessageBuilder(correlationID, messageBody, ImmutableMap.<String, String>of());

        return messageBuilder;
    }

    private static Map<String, String> convertHandlerResponseToProperties(HandlerResponse response) {
        Map<String, String> properties = Maps.newHashMap();

        properties.put(JmsKeys.GMP_HANDLER_RESPONSE_KEY, response.getResponse().name());
        if (response.hasErrorMessage()) {
            properties.put(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
        }
        return properties;
    }

    public static MapMessageBuilder newMessageBuilder(CompletionInformation completionInformation, String correlationID) {
        Map<String, String> messageBody = convertConfigurationToProperties(completionInformation.getCommand().getConfiguration());
        Map<String, String> properties = convertResponseAndCommandToProperties(completionInformation);

        MapMessageBuilder messageBuilder = new StringBasedWithCorrelationIDMessageBuilder(correlationID, messageBody, properties);

        return messageBuilder;
    }


    /**
     * Translates a configuration into properties that will be included in the JMS Message body
     */
    private static Map<String, String> convertConfigurationToProperties(Configuration config) {
        Preconditions.checkArgument(config != null);
        Map<String, String> content = Maps.newHashMap();

        for (ConfigPath path : config.getKeys()) {
            content.put(path.getName(), config.getValue(path));
        }
        return content;
    }

    /**
     * Translates a CompletionInformation into maps to be used in a message
     */
    private static Map<String, String> convertResponseAndCommandToProperties(CompletionInformation completionInformation) {
        Map<String, String> properties = Maps.newHashMap();

        properties.putAll(convertHandlerResponseToProperties(completionInformation.getHandlerResponse()));
        properties.putAll(convertCommandToProperties(completionInformation.getCommand()));

        return ImmutableMap.copyOf(properties);
    }

    private static Map<String, String> convertCommandToProperties(Command command) {
        SequenceCommand sc = command.getSequenceCommand();
        Activity activity = command.getActivity();

        return ImmutableMap.of(
                JmsKeys.GMP_SEQUENCE_COMMAND_KEY, sc.name(),
                JmsKeys.GMP_ACTIVITY_KEY, activity.name());
    }

    public static MapMessageBuilder newMessageBuilder(Command command, String correlationID) {
        Map<String, String> messageBody = Maps.newHashMap();
        Set<ConfigPath> paths = command.getConfiguration().getKeys();
        for (ConfigPath path : paths) {
            messageBody.put(path.getName(), command.getConfiguration().getValue(path));
        }
        Map<String, String> properties = convertCommandToProperties(command);
        return new StringBasedWithCorrelationIDMessageBuilder(correlationID, messageBody, properties);
    }
}
