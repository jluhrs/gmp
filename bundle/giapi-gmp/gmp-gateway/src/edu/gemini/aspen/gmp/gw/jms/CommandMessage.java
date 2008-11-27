package edu.gemini.aspen.gmp.gw.jms;

import edu.gemini.aspen.gmp.commands.api.*;

import javax.jms.MapMessage;
import javax.jms.JMSException;
import javax.jms.Destination;
import java.util.Enumeration;

/**
 *
 */
public class CommandMessage {


    private SequenceCommand _sequenceCommand;
    private Activity _activity;
    private Configuration _configuration;
    private CompletionListener _completionListener;

    private Destination _replyDestination;


    public CommandMessage(MapMessage msg) throws FormatException {

        _sequenceCommand = parseSequenceCommand(msg);

        _activity = parseActivity(msg);

        _configuration = parseConfiguration(msg);

        _completionListener = parseCompletionListener(msg);

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

    public CompletionListener getCompletionListener() {
        return _completionListener;
    }


    private SequenceCommand parseSequenceCommand(MapMessage msg) throws FormatException {
        try {
            String sequenceCommand = msg.getStringProperty(GatewayKeys.SEQUENCE_COMMAND_KEY);
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
            String activity = msg.getStringProperty(GatewayKeys.ACTIVITY_KEY);
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

    private CompletionListener parseCompletionListener(MapMessage msg) throws FormatException {
        try {
            _replyDestination = msg.getJMSReplyTo();

            if (_replyDestination == null) throw
                    new FormatException("Completion information incomplete in message");

            return new GatewayCompletionListener(_replyDestination);
        } catch (JMSException e) {
            throw new FormatException("Message doesn't contain completion listener information");
        }


    }


    public Destination getReplyDestination() {
        return _replyDestination;
    }
}
