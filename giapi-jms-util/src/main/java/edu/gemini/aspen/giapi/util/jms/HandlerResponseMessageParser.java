package edu.gemini.aspen.giapi.util.jms;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.jms.api.FormatException;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

/**
 * Class that can parse a Map message sent over JMS and convert it into a
 * {@link edu.gemini.aspen.giapi.commands.Command}
 */
public class HandlerResponseMessageParser {
    private final MapMessage mapMessage;

    public HandlerResponseMessageParser(Message mapMessage) throws FormatException {
        Preconditions.checkArgument(mapMessage != null, "Message cannot be null");
        if (!(mapMessage instanceof MapMessage)) {
            throw new FormatException("Cannot process a non map message");
        }
        this.mapMessage = (MapMessage)mapMessage;
    }

    public HandlerResponse readResponse() throws FormatException {
        try {
            return parseResponse();
        } catch (JMSException e) {
            throw new FormatException("JMS Exception while decoding the message", e);
        } catch (IllegalArgumentException e) {
            throw new FormatException("Message contains an invalid response");
        } catch (NullPointerException e) {
            throw new FormatException("Message didn't contain a response");
        }
    }

    private HandlerResponse parseResponse() throws FormatException, JMSException {
        String responseType = mapMessage.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY);
        HandlerResponse.Response response = HandlerResponse.Response.valueOf(responseType);

        if (response == HandlerResponse.Response.ERROR) {
            String errorMsg = mapMessage.getString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY);
            return HandlerResponse.createError(errorMsg);
        }
        return HandlerResponse.get(response);
    }
}
