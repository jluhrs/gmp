package edu.gemini.aspen.giapi.commands;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HandlerResponseTest {
    @Test
    public void testHandlerError() {
        String errorMsg = "Error message";
        HandlerResponse error = HandlerResponse.createError(errorMsg);
        assertNotNull(error);
        assertEquals(errorMsg, error.getMessage());
        assertEquals(HandlerResponse.Response.ERROR, error.getResponse());
        assertEquals("ERROR", error.getResponse().getTag());
    }

    @Test
    public void testHasErrorMessage() {
        String errorMsg = "Error message";
        HandlerResponse error = HandlerResponse.createError(errorMsg);
        assertTrue(error.hasErrorMessage());

        error = HandlerResponse.createError(null);
        assertFalse(error.hasErrorMessage());

        assertFalse(HandlerResponse.get(HandlerResponse.Response.ACCEPTED).hasErrorMessage());
    }

    @Test
    public void testHandlerErrorEquality() {
        String errorMsg = "Error message";
        HandlerResponse a = HandlerResponse.createError(errorMsg);
        HandlerResponse b = HandlerResponse.createError(errorMsg);
        HandlerResponse c = HandlerResponse.get(HandlerResponse.Response.ACCEPTED);

        new EqualsTester(a, b, c, null);
    }

    @Test
    public void testHandlerResponseEquality() {
        HandlerResponse a = HandlerResponse.get(HandlerResponse.Response.ACCEPTED);
        HandlerResponse b = HandlerResponse.get(HandlerResponse.Response.ACCEPTED);
        HandlerResponse c = HandlerResponse.get(HandlerResponse.Response.COMPLETED);

        new EqualsTester(a, b, c, null);
    }

    @Test
    public void testHandlerResponseStarted() {
        testResponseType(HandlerResponse.Response.STARTED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotValidResponse() {
        HandlerResponse.get(null);
    }

    private void testResponseType(HandlerResponse.Response responseType) {
        HandlerResponse handlerResponse = HandlerResponse.get(responseType);
        assertNotNull(handlerResponse);
        assertNotNull(handlerResponse.getMessage());
        assertEquals(responseType, handlerResponse.getResponse());
        assertEquals(responseType.getTag(), handlerResponse.getResponse().getTag());
    }

    @Test
    public void testHandlerResponseAccepted() {
        testResponseType(HandlerResponse.Response.ACCEPTED);
    }

    @Test
    public void testHandlerResponseCompleted() {
        testResponseType(HandlerResponse.Response.COMPLETED);
    }

    @Test
    public void testHandlerResponseNoAnswer() {
        testResponseType(HandlerResponse.Response.NOANSWER);
    }

    @Test
    public void testErrorResponseType() {
        HandlerResponse errorResponse = HandlerResponse.get(HandlerResponse.Response.ERROR);
        assertNotNull(errorResponse);
        assertEquals("", errorResponse.getMessage());
        assertEquals(HandlerResponse.Response.ERROR, errorResponse.getResponse());
        assertEquals(HandlerResponse.Response.ERROR.getTag(), errorResponse.getResponse().getTag());
    }

    @Test
    public void testToString() {
        HandlerResponse handlerResponse = HandlerResponse.get(HandlerResponse.Response.ERROR);
        assertEquals("[ERROR {}]", handlerResponse.toString());
        handlerResponse = HandlerResponse.get(HandlerResponse.Response.ACCEPTED);
        assertEquals("[ACCEPTED]", handlerResponse.toString());
    }

    @Test
    public void testToStringWithNullError() {
        HandlerResponse handlerResponse = HandlerResponse.createError(null);
        assertEquals("[ERROR {}]", handlerResponse.toString());
    }
}
