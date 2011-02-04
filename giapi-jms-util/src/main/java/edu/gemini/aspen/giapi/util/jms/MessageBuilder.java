package edu.gemini.aspen.giapi.util.jms;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.StatusVisitor;
import edu.gemini.aspen.giapi.util.jms.status.StatusItemParser;
import edu.gemini.aspen.giapi.util.jms.status.StatusSerializerVisitor;

import javax.jms.*;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.logging.Level;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.copy;

/**
 * Collection of utility methods that will help to transform from JMS messages
 * to GIAPI data structures and vice versa
 */
public final class MessageBuilder {

    private static final Logger LOG = Logger.getLogger(MessageBuilder.class.getName());

    //the following are a bunch of methods to return error messages.

    static String InvalidHandlerResponseMessage() {
        return "Invalid Message type to build HandlerResponse object";
    }

    static String InvalidResponseTypeMessage(String key) {
        return format("Invalid response type contained in the reply", key);
    }

    static String InvalidResponseTypeMessage() {
        return InvalidResponseTypeMessage(null);
    }

    static String InvalidCompletionInformationMessage() {
        return "Invalid Message to construct Completion Information";
    }

    static String InvalidSequenceCommandMessage(String key) {
        return format("Invalid sequence command ", key);
    }

    static String InvalidActivityMessage(String key) {
        return format("Invalid Activity ", key);
    }

    static String InvalidConfigurationMessage() {
        return "Invalid configuration received";
    }

    //a formatter for the error messages
    private static String format(String message, String key) {
        StringBuilder sb = new StringBuilder(message);
        sb.append(" (").append(key).append(")");
        return sb.toString();
    }


    public static HandlerResponse buildHandlerResponse(Message m) throws JMSException {

        if (!(m instanceof MapMessage))
            throw new JMSException(InvalidHandlerResponseMessage());

        MapMessage msg = (MapMessage) m;

        String responseType = msg.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY);

        if (responseType == null) throw new JMSException(InvalidResponseTypeMessage());

        HandlerResponse.Response response;
        try {
            response = HandlerResponse.Response.valueOf(responseType);
        } catch (IllegalArgumentException ex) {
            throw new JMSException(InvalidResponseTypeMessage(responseType));
        }

        if (response == HandlerResponse.Response.ERROR) {
            String errorMsg = msg.getString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY);
            return HandlerResponse.createError(errorMsg);
        }
        return HandlerResponse.get(response);
    }

    public static Message buildHandlerResponseMessage(Session session, HandlerResponse response) throws JMSException {
        MapMessage message = session.createMapMessage();

        //fill in the message
        message.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, response.getResponse().name());

        if (response.getResponse() == HandlerResponse.Response.ERROR) {
            if (response.getMessage() != null) {
                message.setString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
            }
        }
        return message;
    }

    public static Message buildCompletionInformationMessage(Session session, CompletionInformation info) throws JMSException {

        MapMessage reply = session.createMapMessage();

        HandlerResponse response = info.getHandlerResponse();
        if (response != null) {
            reply.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY, response.getResponse().name());
            if (response.getResponse() == HandlerResponse.Response.ERROR) {
                if (response.getMessage() != null) {
                    reply.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
                }
            }
        }

        SequenceCommand command = info.getSequenceCommand();
        if (command != null) {
            reply.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, command.name());
        }

        Activity activity = info.getActivity();
        if (activity != null) {
            reply.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, activity.name());
        }

        Configuration config = info.getConfiguration();
        if (config != null) {
            for (ConfigPath path : config.getKeys()) {
                reply.setString(path.getName(), config.getValue(path));
            }
        }
        return reply;
    }

    public static CompletionInformation buildCompletionInformation(Message m) throws JMSException {
        //reconstruct CompletionInfo based on the message

        if (!(m instanceof MapMessage)) {
            throw new JMSException(InvalidCompletionInformationMessage());
        }

        MapMessage msg = (MapMessage) m;

        //get the Handler Response.
        String key = msg.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY);
        HandlerResponse handlerResponse = null;
        if (key != null) {
            HandlerResponse.Response response;
            try {
                response = HandlerResponse.Response.valueOf(key);
            } catch (IllegalArgumentException ex) {
                throw new JMSException(InvalidResponseTypeMessage(key));
            }

            if (response == HandlerResponse.Response.ERROR) {
                String errMsg = msg.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY);
                handlerResponse = HandlerResponse.createError(errMsg);
            } else {
                handlerResponse = HandlerResponse.get(response);
            }
        }

        //get the sequence command
        SequenceCommand sc = null;
        key = msg.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY);
        if (key != null) {
            try {
                sc = SequenceCommand.valueOf(key);
            } catch (IllegalArgumentException ex) {
                throw new JMSException(InvalidSequenceCommandMessage(key));
            }
        }

        //get the activity
        Activity activity = null;
        key = msg.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY);
        if (key != null) {
            try {
                activity = Activity.valueOf(key);
            } catch (IllegalArgumentException ex) {
                throw new JMSException(InvalidActivityMessage(key));
            }
        }

        //get configuration
        Configuration config = null;
        Enumeration names = msg.getMapNames();

        // FIXME Here is a potential NPE
        if (names.hasMoreElements()) {
            config = DefaultConfiguration.emptyConfiguration();
            DefaultConfiguration.Builder builder = copy(config);
            while (names.hasMoreElements()) {
                Object o = names.nextElement();
                if (!(o instanceof String)) {
                    throw new JMSException(InvalidConfigurationMessage());
                }
                String path = (String) o;
                String value = msg.getString(path);
                builder.withPath(configPath(path), value);
            }
            config = builder.build();
        }

        return new CompletionInformation(handlerResponse, sc, activity, config);
    }

    public static Set<String> buildStatusNames(Message m) throws JMSException {
        if (!(m instanceof BytesMessage)) return null;

        BytesMessage bm = (BytesMessage) m;

        Set<String> names = new TreeSet<String>();
        int numNames = bm.readInt();
        for (int i = 0; i < numNames; i++) {
            names.add(bm.readUTF());
        }
        return names;
    }

    public static StatusItem buildStatusItem(Message m) throws JMSException {
        if (!(m instanceof BytesMessage)) return null;

        BytesMessage bm = (BytesMessage) m;
        try {
            return StatusItemParser.parse(bm);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }


    public static Message buildStatusItemMessage(Session session, StatusItem item) throws JMSException {

        BytesMessage bm = session.createBytesMessage();

        if (item == null) return bm; //an empty message. 

        StatusVisitor serializer = new StatusSerializerVisitor(bm);

        try {
            item.accept(serializer);
        } catch (JMSException e) {
            throw e;
        } catch (Exception e) {
            //this shouldn't happen, since the serializer only throws JMS Exceptions.
            LOG.log(Level.SEVERE, "Received unexpected exception ", e);
        }

        return bm;
    }

}
