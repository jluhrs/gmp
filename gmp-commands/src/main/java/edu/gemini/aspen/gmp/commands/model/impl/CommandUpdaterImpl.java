package edu.gemini.aspen.gmp.commands.model.impl;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.giapi.commands.HandlerResponse;

/**
 * Implementation of the {@link edu.gemini.aspen.giapi.commands.CommandUpdater}
 * interface. It notifies {@link ActionManager}
 * whenever new updates are available to process. 
 */
public class CommandUpdaterImpl implements CommandUpdater {

    private final ActionManager _manager;

    public CommandUpdaterImpl(ActionManager manager) {
        Preconditions.checkArgument(manager != null, "ActionManager cannot be null");
        _manager = manager;
    }

    @Override
    public void updateOcs(int actionId, HandlerResponse response) {
        //make the completion information available for the Action Manager
        //to notify the clients.
        _manager.registerCompletionInformation(actionId, response);
    }
}
