package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;

/**
 * A test suite for the handler response analizer
 */
public class HandlerResponseAnalizerTest {

    HandlerResponseAnalizer _analizer;

    HandlerResponse[] _responses;
    
    Map<HandlerResponse.Response, HandlerResponse> _responseMap;

    @Before
    public void setUp() {
        _analizer = new HandlerResponseAnalizer();
        _responses = new HandlerResponse[] {
                HandlerResponse.ACCEPTED,
                HandlerResponse.STARTED,
                HandlerResponse.COMPLETED,
                HandlerResponse.NOANSWER,
                HandlerResponse.createError("Error")
        };

        _responseMap = new HashMap<HandlerResponse.Response, HandlerResponse>();
        _responseMap.put(HandlerResponse.Response.ACCEPTED, _responses[0]);
        _responseMap.put(HandlerResponse.Response.STARTED, _responses[1]);
        _responseMap.put(HandlerResponse.Response.COMPLETED, _responses[2]);
        _responseMap.put(HandlerResponse.Response.NOANSWER, _responses[3]);
        _responseMap.put(HandlerResponse.Response.ERROR, _responses[4]);
    }


    /**
     * Check that the analizer works with a single entry on it
     */
    @Test
    public void testResultsWithOneEntry() {
        for (HandlerResponse response: _responses) {
            HandlerResponseAnalizer analizer = new HandlerResponseAnalizer();
            analizer.addResponse(response);
            assertEquals(response, analizer.getSummaryResponse());
        }
    }

    /**
     * Validates the analizer gets the right response when all the elements are the same
     */
    @Test
    public void testResultWithSameElements() {

        int nElem = 10;

        for (HandlerResponse.Response response: HandlerResponse.Response.values()) {

            HandlerResponseAnalizer analizer = new HandlerResponseAnalizer();
            for (int i = 0; i < nElem ; i++) {
                analizer.addResponse(_responseMap.get(response));
            }
            assertEquals(_responseMap.get(response), analizer.getSummaryResponse());
        }
    }


    /**
     * If there is one no-answer within the responses, then the whole response
     * is no-answer, regardless of the rest
     */
    @Test
    public void testGetNoAnswer() {

        for (HandlerResponse.Response response: HandlerResponse.Response.values()) {
            _analizer.addResponse(_responseMap.get(response));
        }

        assertEquals(_responseMap.get(HandlerResponse.Response.NOANSWER), _analizer.getSummaryResponse());
    }



    /**
     * If there is an error then the whole response
     * is an error, regardless of the rest
     */
    @Test
    public void testGetError() {

        for (HandlerResponse.Response response: HandlerResponse.Response.values()) {
            if (response != HandlerResponse.Response.NOANSWER)
                _analizer.addResponse(_responseMap.get(response));
        }

        assertEquals(_responseMap.get(HandlerResponse.Response.ERROR), _analizer.getSummaryResponse());
    }


    /**
     * This test assumes we get COMPLETED, ACCEPTED and STARTED responses.
     */
    @Test
    public void testStarted() {
        _analizer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        _analizer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        _analizer.addResponse(_responseMap.get(HandlerResponse.Response.STARTED));
        _analizer.addResponse(_responseMap.get(HandlerResponse.Response.ACCEPTED));
        _analizer.addResponse(_responseMap.get(HandlerResponse.Response.ACCEPTED));
        assertEquals(_responseMap.get(HandlerResponse.Response.STARTED), _analizer.getSummaryResponse());

    }

    /**
     * This test assumes we get COMPLETED and ACCEPTED responses.
     */
    @Test
    public void testAccepted() {
        _analizer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        _analizer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        _analizer.addResponse(_responseMap.get(HandlerResponse.Response.ACCEPTED));
        _analizer.addResponse(_responseMap.get(HandlerResponse.Response.ACCEPTED));
        assertEquals(_responseMap.get(HandlerResponse.Response.ACCEPTED), _analizer.getSummaryResponse());

    }

    /**
     * This test assumes we get COMPLETED and ACCEPTED responses.
     */
    @Test
    public void testCompleted() {
        _analizer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        _analizer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        _analizer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        assertEquals(_responseMap.get(HandlerResponse.Response.COMPLETED), _analizer.getSummaryResponse());

    }






}

