package edu.gemini.aspen.gmp.commands.model.impl;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.gmp.commands.model.ActionManager;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

/**
 * Implementation of the {@link edu.gemini.aspen.giapi.commands.CommandUpdater}
 * interface. It notifies {@link ActionManager}
 * whenever new updates are available to process. 
 */
@Component
@Instantiate
@Provides
public class CommandUpdaterImpl implements CommandUpdater {

    private final ActionManager _manager;

    public CommandUpdaterImpl(@Requires ActionManager manager) {
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
