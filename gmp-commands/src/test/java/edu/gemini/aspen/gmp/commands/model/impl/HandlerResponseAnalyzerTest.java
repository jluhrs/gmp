package edu.gemini.aspen.gmp.commands.model.impl;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * A test suite for the handler response analizer
 */
public class HandlerResponseAnalyzerTest {

    HandlerResponseAnalyzer _analyzer;

    HandlerResponse[] _responses;
    
    Map<HandlerResponse.Response, HandlerResponse> _responseMap;

    @Before
    public void setUp() {
        _analyzer = new HandlerResponseAnalyzer();
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
            HandlerResponseAnalyzer analyzer = new HandlerResponseAnalyzer();
            analyzer.addResponse(response);
            assertEquals(response, analyzer.getSummaryResponse());
        }
    }

    /**
     * Validates the analizer gets the right response when all the elements are the same
     */
    @Test
    public void testResultWithSameElements() {

        int nElem = 10;

        for (HandlerResponse.Response response: HandlerResponse.Response.values()) {

            HandlerResponseAnalyzer analyzer = new HandlerResponseAnalyzer();
            for (int i = 0; i < nElem ; i++) {
                analyzer.addResponse(_responseMap.get(response));
            }
            assertEquals(_responseMap.get(response), analyzer.getSummaryResponse());
        }
    }

    /**
     * If there is one no-answer within the responses, then the whole response
     * is no-answer, regardless of the rest
     */
    @Test
    public void testGetNoAnswer() {

        for (HandlerResponse.Response response: HandlerResponse.Response.values()) {
            _analyzer.addResponse(_responseMap.get(response));
        }

        assertEquals(_responseMap.get(HandlerResponse.Response.NOANSWER), _analyzer.getSummaryResponse());
    }

    /**
     * If there is an error then the whole response
     * is an error, regardless of the rest
     */
    @Test
    public void testGetError() {

        for (HandlerResponse.Response response: HandlerResponse.Response.values()) {
            if (response != HandlerResponse.Response.NOANSWER)
                _analyzer.addResponse(_responseMap.get(response));
        }

        assertEquals(_responseMap.get(HandlerResponse.Response.ERROR), _analyzer.getSummaryResponse());
    }

    /**
     * This test assumes we get COMPLETED, ACCEPTED and STARTED responses.
     */
    @Test
    public void testStarted() {
        _analyzer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        _analyzer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        _analyzer.addResponse(_responseMap.get(HandlerResponse.Response.STARTED));
        _analyzer.addResponse(_responseMap.get(HandlerResponse.Response.ACCEPTED));
        _analyzer.addResponse(_responseMap.get(HandlerResponse.Response.ACCEPTED));
        assertEquals(_responseMap.get(HandlerResponse.Response.STARTED), _analyzer.getSummaryResponse());

    }

    /**
     * This test assumes we get COMPLETED and ACCEPTED responses.
     */
    @Test
    public void testAccepted() {
        _analyzer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        _analyzer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        _analyzer.addResponse(_responseMap.get(HandlerResponse.Response.ACCEPTED));
        _analyzer.addResponse(_responseMap.get(HandlerResponse.Response.ACCEPTED));
        assertEquals(_responseMap.get(HandlerResponse.Response.ACCEPTED), _analyzer.getSummaryResponse());

    }

    /**
     * This test assumes we get COMPLETED and ACCEPTED responses.
     */
    @Test
    public void testCompleted() {
        _analyzer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        _analyzer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        _analyzer.addResponse(_responseMap.get(HandlerResponse.Response.COMPLETED));
        assertEquals(_responseMap.get(HandlerResponse.Response.COMPLETED), _analyzer.getSummaryResponse());

    }
}

