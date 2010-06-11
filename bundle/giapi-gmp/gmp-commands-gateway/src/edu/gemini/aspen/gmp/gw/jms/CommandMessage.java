package edu.gemini.aspen.gmp.gw.jms;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;

import javax.jms.MapMessage;
import javax.jms.JMSException;
import java.util.Enumeration;

/**
 *
 */
public class CommandMessage {


    private SequenceCommand _sequenceCommand;
    private Activity _activity;
    private Configuration _configuration;


    public CommandMessage(MapMessage msg) throws FormatException {
        _sequenceCommand = parseSequenceCommand(msg);

        _activity = parseActivity(msg);

        _configuration = parseConfiguration(msg);

        parseCompletionListener(msg);
    }



    public SequenceCommand getSequenceCommand() {
        return _sequenceCommand;
    }

    public Activity getActivity() {
        return _activity;
    }

    public Configuration getConfiguration() {
        return _configuration;
    }


    private SequenceCommand parseSequenceCommand(MapMessage msg) throws FormatException {
        try {
            String sequenceCommand = msg.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY);
            return SequenceCommand.valueOf(sequenceCommand);
        } catch (JMSException e) {
            throw new FormatException("Message didn't contain a sequence command");
        } catch (IllegalArgumentException e) {
            throw new FormatException("Message contains an invalid sequence command");
        } catch (NullPointerException e) {
            throw new FormatException("Message didn't contain a sequence command");
        }
    }

    private Activity parseActivity(MapMessage msg) throws FormatException {
        try {
            String activity = msg.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY);
            return Activity.valueOf(activity);
        } catch (JMSException e) {
            throw new FormatException("Message didn't contain an activity");
        } catch (IllegalArgumentException e) {
            throw new FormatException("Message contains an invalid activity");
        } catch (NullPointerException e) {
            throw new FormatException("Message didn't contain an activity");
        }
    }


    private Configuration parseConfiguration(MapMessage msg) throws FormatException {

        try {
            Enumeration enumeration = msg.getMapNames();
            DefaultConfiguration config = new DefaultConfiguration();

            while(enumeration.hasMoreElements()) {
                Object o = enumeration.nextElement();
                if (o instanceof String) {
                    String key = (String)o;
                    config.put(new ConfigPath(key), msg.getString(key));
                }
            }
            return config;
        } catch (JMSException e) {
            throw new FormatException("Message didn't contain a configuration");
        }
    }

    private void parseCompletionListener(MapMessage msg) throws FormatException {
        try {
            if (msg.getJMSReplyTo() == null) throw
                    new FormatException("Completion information incomplete in message");
        } catch (JMSException e) {
            throw new FormatException("Message doesn't contain completion listener information");
        }
    }
}
