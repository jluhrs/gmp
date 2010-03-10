package edu.gemini.aspen.gmp.util.jms;

import edu.gemini.aspen.gmp.commands.api.*;
import edu.gemini.aspen.gmp.status.StatusItem;
import edu.gemini.aspen.gmp.status.StatusVisitor;
import edu.gemini.aspen.gmp.util.commands.HandlerResponseImpl;
import edu.gemini.aspen.gmp.util.commands.CompletionInformationImpl;
import edu.gemini.aspen.gmp.util.jms.status.StatusItemParser;
import edu.gemini.aspen.gmp.util.jms.status.StatusSerializerVisitor;

import javax.jms.*;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Collection of utility methods that will help to transform from JMS messages
 * to GMP data structures and viceversa
 */
public class GmpJmsUtil {

    private static final Logger LOG = Logger.getLogger(GmpJmsUtil.class.getName());

    public static HandlerResponse buildHandlerResponse(Message m) throws JMSException {

        if (!(m instanceof MapMessage))
            throw new JMSException("Invalid Message type to build HandlerResponse object");

        MapMessage msg = (MapMessage) m;

        String responseType = msg.getString(GmpKeys.GMP_HANDLER_RESPONSE_KEY);
        HandlerResponse.Response response;
        try {
            response = HandlerResponse.Response.valueOf(responseType);
        } catch (IllegalArgumentException ex) {
            throw new JMSException("Invalid response type contained in the reply");
        }

        if (response != null) {
            if (response == HandlerResponse.Response.ERROR) {
                String errorMsg = msg.getString(GmpKeys.GMP_HANDLER_RESPONSE_ERROR_KEY);
                return HandlerResponseImpl.createError(errorMsg);
            }
            return HandlerResponseImpl.create(response);
        }
        return null;
    }

    public static Message buildHandlerResponseMessage(Session session, HandlerResponse response) throws JMSException {
        MapMessage message = session.createMapMessage();

        //fill in the message
        message.setString(GmpKeys.GMP_HANDLER_RESPONSE_KEY, response.getResponse().name());

        if (response.getResponse() == HandlerResponse.Response.ERROR) {
            if (response.getMessage() != null) {
                message.setString(GmpKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
            }
        }
        return message;
    }

    public static Message buildCompletionInformationMessage(Session session, CompletionInformation info) throws JMSException {

        MapMessage reply = session.createMapMessage();

        HandlerResponse response = info.getHandlerResponse();
        if (response != null) {
            reply.setStringProperty(GmpKeys.GMP_HANDLER_RESPONSE_KEY, response.getResponse().name());
            if (response.getResponse() == HandlerResponse.Response.ERROR) {
                if (response.getMessage() != null) {
                    reply.setStringProperty(GmpKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
                }
            }
        }

        SequenceCommand command = info.getSequenceCommand();
        if (command != null) {
            reply.setStringProperty(GmpKeys.GMP_SEQUENCE_COMMAND_KEY, command.name());
        }

        Activity activity = info.getActivity();
        if (activity != null) {
            reply.setStringProperty(GmpKeys.GMP_ACTIVITY_KEY, activity.name());
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
            throw new JMSException("Invalid Message to construct Completion Information");
        }

        MapMessage msg = (MapMessage) m;

        //get the Handler Response.
        String key = msg.getStringProperty(GmpKeys.GMP_HANDLER_RESPONSE_KEY);
        HandlerResponse handlerResponse = null;
        if (key != null) {
            HandlerResponse.Response response;
            try {
                response = HandlerResponse.Response.valueOf(key);
            } catch (IllegalArgumentException ex) {
                throw new JMSException("Invalid response type contained in the reply: " + key);
            }

            if (response == HandlerResponse.Response.ERROR) {
                String errMsg = msg.getStringProperty(GmpKeys.GMP_HANDLER_RESPONSE_ERROR_KEY);
                handlerResponse = HandlerResponseImpl.createError(errMsg);
            } else {
                handlerResponse = HandlerResponseImpl.create(response);
            }
        }

        //get the sequence command
        SequenceCommand sc = null;
        key = msg.getStringProperty(GmpKeys.GMP_SEQUENCE_COMMAND_KEY);
        if (key != null) {
            try {
                sc = SequenceCommand.valueOf(key);
            } catch (IllegalArgumentException ex) {
                throw new JMSException("Invalid sequence command: " + key);
            }
        }

        //get the activity
        Activity activity = null;
        key = msg.getStringProperty(GmpKeys.GMP_ACTIVITY_KEY);
        if (key != null) {
            try {
                activity = Activity.valueOf(key);
            } catch (IllegalArgumentException ex) {
                throw new JMSException("Invalid Activity: " + key);
            }
        }

        //get configuration
        DefaultConfiguration config = null;
        Enumeration names = msg.getMapNames();

        if (names.hasMoreElements()) {
            config = new DefaultConfiguration();
        }

        while (names.hasMoreElements()) {
            Object o = names.nextElement();
            if (!(o instanceof String)) {
                throw new JMSException("Invalid configuration received");
            }
            String path = (String) o;
            String value = msg.getString(path);
            config.put(new ConfigPath(path), value);
        }

        return new CompletionInformationImpl(handlerResponse, sc, activity, config);
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
