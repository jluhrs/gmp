package edu.gemini.aspen.gmp.commands.model.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import edu.gemini.aspen.giapi.commands.HandlerResponse;

import java.util.List;

/**
 * A class to analyze handler responses and return a single representation when
 * multiple responses are obtained.
 */
public class HandlerResponseAnalyzer {


    private final List<HandlerResponse> _responses = Lists.newArrayList();

    /**
     * Adds the response to the collection of responses to be analized
     *
     * @param response the response to be considered when creating a summary
     */
    public void addResponse(HandlerResponse response) {
        Preconditions.checkArgument(response != null, "Cannot analyze a null response");
        _responses.add(response);
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
            if (response == HandlerResponse.NOANSWER) {
                return response;
            }
        }

        //if there is an error, the entire stuff is an error
        for (HandlerResponse response : _responses) {
            if (response.getResponse() == HandlerResponse.Response.ERROR) {
                return response;
            }
        }
        //no errors, so next option is at least one started, the entire thing is started
        for (HandlerResponse response : _responses) {
            if (response == HandlerResponse.STARTED) {
                return response;
            }
        }

        //no errors, and no started....let's see if we got an "accepted"
        for (HandlerResponse response : _responses) {
            if (response == HandlerResponse.ACCEPTED) {
                return response;
            }
        }

        return HandlerResponse.COMPLETED;
    }
}
