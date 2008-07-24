package edu.gemini.aspen.gmp.broker.jms;

import javax.jms.*;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.broker.impl.GMPKeys;
import edu.gemini.aspen.gmp.broker.commands.HandlerResponseImpl;

/**
 */
public class JMSUtil {

    public static HandlerResponse buildHandlerResponse(MapMessage msg) throws JMSException {

        String responseType = msg.getString(GMPKeys.GMP_HANDLER_RESPONSE_KEY);
        HandlerResponse.Response response; 
        try {
            response = HandlerResponse.Response.valueOf(responseType);
        } catch(IllegalArgumentException ex) {
            throw new JMSException("Invalid response type contained in the reply");
        }

        if (response != null) {
            if (response == HandlerResponse.Response.ERROR) {
                String errorMsg = msg.getString(GMPKeys.GMP_HANDLER_RESPONSE_ERROR_KEY);
                return HandlerResponseImpl.create(response, errorMsg);
            }
            return HandlerResponseImpl.create(response);
        }
        return null;
    }
}
