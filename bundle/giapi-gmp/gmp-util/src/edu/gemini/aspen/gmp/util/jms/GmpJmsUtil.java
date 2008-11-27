package edu.gemini.aspen.gmp.util.jms;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.commands.api.CompletionInformation;
import edu.gemini.aspen.gmp.util.commands.HandlerResponseImpl;

import javax.jms.MapMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Collection of utility methods that will help to transform from JMS messages
 * to GMP data structures and viceversa
 */
public class GmpJmsUtil {

    public static HandlerResponse buildHandlerResponse(Message m) throws JMSException {

        if (!(m instanceof MapMessage))
            throw new JMSException("Invalid Message type to build HandlerResponse object");

        MapMessage msg = (MapMessage)m;
        
        String responseType = msg.getString(GmpKeys.GMP_HANDLER_RESPONSE_KEY);
        HandlerResponse.Response response;
        try {
            response = HandlerResponse.Response.valueOf(responseType);
        } catch(IllegalArgumentException ex) {
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

        if (response.getResponse() == HandlerResponse.Response.ERROR)  {
            if (response.getMessage() != null) {
                message.setString(GmpKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
            }
        }
        return message;
    }

    public static CompletionInformation buildCompletionInformation(Message m) throws JMSException {
        return null;
   }

}
