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

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.List;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;

/**
 * Class that can parse a Map message sent over JMS and convert it into a
 * {@link edu.gemini.aspen.giapi.commands.Command}
 */
public class CommandMessageParser {

    private CommandMessageParser() {
    }

    public static Command readCommand(MapMessage msg) throws FormatException {
        Preconditions.checkArgument(msg != null, "Message cannot be null");
        try {
            return parseCommand(msg);
        } catch (JMSException e) {
            throw new FormatException("JMS Exception while decoding the message", e);
        }
    }

    private static Command parseCommand(MapMessage msg) throws JMSException {
        SequenceCommand sequenceCommand = parseSequenceCommand(msg);
        Activity activity = parseActivity(msg);
        Configuration configuration = parseConfiguration(msg);
        
        return new Command(sequenceCommand, activity, configuration);
    }

    static SequenceCommand parseSequenceCommand(MapMessage msg) throws FormatException, JMSException {
        try {
            String sequenceCommand = msg.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY);
            return SequenceCommand.valueOf(sequenceCommand);
        } catch (IllegalArgumentException e) {
            throw new FormatException("Message contains an invalid sequence command");
        } catch (NullPointerException e) {
            throw new FormatException("Message didn't contain a sequence command");
        }
    }

    static Activity parseActivity(MapMessage msg) throws FormatException, JMSException {
        try {
            String activity = msg.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY);
            return Activity.valueOf(activity);
        } catch (IllegalArgumentException e) {
            throw new FormatException("Message contains an invalid activity");
        } catch (NullPointerException e) {
            throw new FormatException("Message didn't contain an activity");
        }
    }

    static Configuration parseConfiguration(MapMessage msg) throws FormatException, JMSException {
        List<String> enumeration= ImmutableList.copyOf(Iterators.forEnumeration(msg.getMapNames()));

        DefaultConfiguration.Builder builder = configurationBuilder();

        for (String path: enumeration) {
             builder.withPath(configPath(path), msg.getString(path));
        }
        return builder.build();

    }
}
