package edu.gemini.aspen.gmp.broker.jms;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.commands.api.Configuration;
import edu.gemini.aspen.gmp.commands.api.ConfigPath;
import edu.gemini.aspen.gmp.commands.api.ConfigPathNavigator;
import edu.gemini.aspen.gmp.broker.commands.Action;
import edu.gemini.aspen.gmp.broker.commands.ActionSender;
import edu.gemini.aspen.gmp.broker.commands.ActionMessage;
import edu.gemini.aspen.gmp.broker.commands.HandlerResponseImpl;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * An Action Sender specific to handle the complexity of the Apply Sequence Command.
 */
public class ApplyActionSender implements ActionSender {

    /**
     * A class to analize handler responses and return a single representation when
     * multiple responses are obtained.
     */
    private class HandlerResponseAnalizer {

        List<HandlerResponse> _responses = new ArrayList<HandlerResponse>();

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

            //if there is a non-answer, means one part of the configuration couldn't get handled.
            //Return NOANSWE
            //TODO: Fix this code. The list of responses should be sorted and getting the summary
            //should be straightforward.
            for (HandlerResponse response : _responses) {
                if (response.getResponse() == HandlerResponse.Response.NOANSWER) return response;
            }

            //if there is an error, the entire stuff is an error
            for (HandlerResponse response : _responses) {
                if (response.getResponse() == HandlerResponse.Response.ERROR) return response;
            }
            //no errors, so next option is at least one started, the entire thing is started
            for (HandlerResponse response : _responses) {
                if (response.getResponse() == HandlerResponse.Response.STARTED) return response;
            }
            return HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED);
        }
    }

    /**
     * This method takes the given action and converts it into a message to
     * be dispatched over the network
     *
     * @param action The action to be sent via the network, it should be
     *               an Apply Sequence command action.
     * @return HandlerResponse associated to the given action. If no response is
     *         received, an NOANSWER HandlerResponse is returned. If the action
     *         is not an Apply Sequence command, an ERROR HandlerResponse is
     *         received
     */
    public HandlerResponse send(Action action) {
        Configuration config = action.getConfiguration();
        if (config == null)
            return HandlerResponseImpl.createError("No configuration present for Apply Sequence command");

        return getResponse(action, config, ConfigPath.EMPTY_PATH);
    }

    /**
     * Auxiliary method to recursively decompose a Configuration to be sent to the appropriate
     * handlers.
     * @param action The action to be sent
     * @param config current configuration being analized
     * @param path current path level in the configuration
     * @return a HandlerResponse representing the result of sending the configuration. If
     * the configuration is not handled, this call will try to decompose the configuration
     * in smaller units in an attempt to see if it can be handled by smaller handlers.
     */

    private HandlerResponse getResponse(Action action, Configuration config, ConfigPath path) {

        if (config == null) return HandlerResponseImpl.createError("Can't get a reply");

        ConfigPathNavigator navigator = new ConfigPathNavigator(config);

        Set<ConfigPath> configPathSet = navigator.getChildPaths(path);

        if (configPathSet.size() <= 0) {
            return HandlerResponseImpl.create(HandlerResponse.Response.NOANSWER);
        }

        //this analizer will get the result answer from this part of the configuration
        HandlerResponseAnalizer analizer = new HandlerResponseAnalizer();

        for (ConfigPath cp : configPathSet) {
            //get the subconfiguration
            Configuration c = config.getSubConfiguration(cp);
            //create an action message to dispatch this configuration over the network
            ActionMessage m = ActionMessageFactory.create(action, cp);
            m.setConfiguration(c);
            //send it
            HandlerResponse response = m.send();
            //if there are no handlers, recursively decompose this config in
            //smaller units if possible, and return the answer.
            if (response.getResponse() == HandlerResponse.Response.NOANSWER) {
                response = getResponse(action, c, cp);
            }

            //if the answer is still NOANSWER, return inmediately, there is no one
            //that can process this part of the configuration. 
            if (response.getResponse() == HandlerResponse.Response.NOANSWER) {
                return response;
            }
            analizer.addResponse(response);
        }
        return analizer.getSummaryResponse();
    }
}
