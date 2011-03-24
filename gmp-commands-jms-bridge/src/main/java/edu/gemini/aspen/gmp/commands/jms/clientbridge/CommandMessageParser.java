package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.DefaultConfiguration;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.FormatException;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.List;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;

/**
 * Class that can parse a Map message sent over JMS and convert it into a
 * {@link edu.gemini.aspen.giapi.commands.Command}
 */
class CommandMessageParser {
    private final MapMessage mapMessage;

    CommandMessageParser(Message mapMessage) throws FormatException {
        Preconditions.checkArgument(mapMessage != null, "Message cannot be null");
        if (!(mapMessage instanceof MapMessage)) {
            throw new FormatException("Cannot process a non map message");
        }
        this.mapMessage = (MapMessage)mapMessage;
    }

    Command readCommand() throws FormatException {
        try {
            return parseCommand();
        } catch (JMSException e) {
            throw new FormatException("JMS Exception while decoding the message", e);
        } catch (IllegalArgumentException e) {
            throw new FormatException("Message contains an invalid command");
        } catch (NullPointerException e) {
            throw new FormatException("Message didn't contain a command");
        }
    }

    private Command parseCommand() throws JMSException {
        if (mapMessage.getJMSCorrelationID() == null) {
            throw new FormatException("Cannot process a message without correlationID");
        }
        SequenceCommand sequenceCommand = parseSequenceCommand();
        Activity activity = parseActivity();
        Configuration configuration = parseConfiguration();

        return new Command(sequenceCommand, activity, configuration);
    }

    private SequenceCommand parseSequenceCommand() throws FormatException, JMSException {
        String sequenceCommand = mapMessage.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY);
        return SequenceCommand.valueOf(sequenceCommand);
    }

    private Activity parseActivity() throws FormatException, JMSException {
        String activity = mapMessage.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY);
        return Activity.valueOf(activity);
    }

    private Configuration parseConfiguration() throws FormatException, JMSException {
        List<String> mapProperties = ImmutableList.copyOf(Iterators.forEnumeration(mapMessage.getMapNames()));

        DefaultConfiguration.Builder builder = configurationBuilder();

        for (String path : mapProperties) {
            builder.withConfiguration(path, mapMessage.getString(path));
        }
        return builder.build();

    }
}
