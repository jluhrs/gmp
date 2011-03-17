package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.gmp.commands.model.*;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.impl.ActionManager;

import java.util.Set;

/**
 * Sequence Command executor for the APPLY Sequence Command. It's job
 * APPLY is more complex than normal sequence commands since an instrument can
 * have more than one handler to deal with a particular APPLY configuration.
 * <p/>
 * This executor attempts to decompose the APPLY configuration to find
 * the handler(s) that will process it.
 */
public class ApplySenderExecutor implements SequenceCommandExecutor {

    private final ActionMessageBuilder _actionMessageBuilder;
    private final ActionManager _actionManager;

    static final String ERROR_MSG = "No configuration present for Apply Sequence command";

    public ApplySenderExecutor(ActionMessageBuilder builder, ActionManager manager) {
        _actionMessageBuilder = builder;
        _actionManager = manager;
    }

    @Override
    public HandlerResponse execute(Action action, ActionSender sender) {

        Configuration config = action.getCommand().getConfiguration();
        if (config.isEmpty()) {
            return HandlerResponse.createError(ERROR_MSG);
        } else {
            return getResponse(action, config, ConfigPath.EMPTY_PATH, sender);
        }
    }


    /**
     * Auxiliary method to recursively decompose a Configuration to be sent to
     * the appropriate handlers.
     *
     * @param action The action to be sent
     * @param config current configuration being analyzed
     * @param path   current path level in the configuration
     * @param sender A Map Sender object that will send this message and will
     *               get an answer.
     * @return a HandlerResponse representing the result of sending the
     *         configuration. If there are no handlers for this configuration,
     *         this call will try to decompose the configuration in smaller units
     *         in an attempt to see if it can be handled by other handlers.
     */

    private HandlerResponse getResponse(Action action, Configuration config,
                                        ConfigPath path, ActionSender sender) {

        ConfigPathNavigator navigator = new ConfigPathNavigator(config);
        Set<ConfigPath> configPathSet = navigator.getChildPaths(path);

        if (configPathSet.size() <= 0) {
            return HandlerResponse.NOANSWER;
        }

        //this analyzer will get the result answer from this part of the configuration
        HandlerResponseAnalizer analyzer = new HandlerResponseAnalizer();

        for (ConfigPath cp : configPathSet) {
            //get the subconfiguration
            Configuration c = config.getSubConfiguration(cp);

            ActionMessage am = _actionMessageBuilder.buildActionMessage(action, cp);

            HandlerResponse response = sender.send(am);

            //if the response is started, there is one handler that will
            //provide answer to this action later. Notify the action
            //manager about this
            if (response == HandlerResponse.STARTED) {
                _actionManager.increaseRequiredResponses(action);
            }

            //if there are no handlers, recursively decompose this config in
            //smaller units if possible, and return the answer.
            if (response == HandlerResponse.NOANSWER) {
                response = getResponse(action, c, cp, sender);
            }

            //if the answer is still NOANSWER, return immediately, there is no one
            //that can process this part of the configuration.
            if (response == HandlerResponse.NOANSWER) {
                return response;
            }
            analyzer.addResponse(response);
        }
        return analyzer.getSummaryResponse();
    }

}
