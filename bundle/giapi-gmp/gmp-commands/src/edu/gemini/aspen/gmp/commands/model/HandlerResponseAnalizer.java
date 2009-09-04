package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.util.commands.HandlerResponseImpl;

import java.util.List;
import java.util.ArrayList;

/**
 * A class to analize handler responses and return a single representation when
 * multiple responses are obtained.
 */
public class HandlerResponseAnalizer {


    private List<HandlerResponse> _responses = new ArrayList<HandlerResponse>();

    /**
     * Adds the response to the collection of responses to be analized
     *
     * @param response the response to be considered when creating a summary
     */
    public void addResponse(HandlerResponse response) {
        if (response != null) {
            _responses.add(response);
        }
    }

    /**
     * Get the handler response that best summarizes the collection
     * of responses obtained
     *
     * @return a HandlerResponse that summarizes all the responses in this collection
     */
    public HandlerResponse getSummaryResponse() {

        //TODO: Fix this code. The list of responses should be sorted and getting the summary
        //should be straightforward.
        
        //if there is a non-answer, means one part of the configuration couldn't get handled.
        //Return NOANSWER
        for (HandlerResponse response : _responses) {
            if (response.getResponse() == HandlerResponse.Response.NOANSWER)
                return response;
        }

        //if there is an error, the entire stuff is an error
        for (HandlerResponse response : _responses) {
            if (response.getResponse() == HandlerResponse.Response.ERROR)
                return response;
        }
        //no errors, so next option is at least one started, the entire thing is started
        for (HandlerResponse response : _responses) {
            if (response.getResponse() == HandlerResponse.Response.STARTED)
                return response;
        }

        //no errors, and no started....let's see if we got an "accepted"
        for (HandlerResponse response : _responses) {
            if (response.getResponse() == HandlerResponse.Response.ACCEPTED)
                return response;
        }

        return HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED);
    }
}
