package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CommandMessageSerializerTest {
    @Test
    public void testConvertHandlerResponseToProperties() {
        HandlerResponse response = HandlerResponse.get(HandlerResponse.Response.ACCEPTED);

        Map<String, String> messageContents = CommandMessageSerializer.convertHandlerResponseToProperties(response);

        assertEquals(HandlerResponse.Response.ACCEPTED.toString(), messageContents.get(JmsKeys.GMP_HANDLER_RESPONSE_KEY));
    }

    @Test
    public void testConvertHandlerErrorResponseToProperties() {
        String errorMsg = "Error Message";

        HandlerResponse response = HandlerResponse.createError(errorMsg);

        Map<String, String> messageContents = CommandMessageSerializer.convertHandlerResponseToProperties(response);

        assertEquals(HandlerResponse.Response.ERROR.toString(), messageContents.get(JmsKeys.GMP_HANDLER_RESPONSE_KEY));
        assertEquals(errorMsg, messageContents.get(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY));
    }
}
