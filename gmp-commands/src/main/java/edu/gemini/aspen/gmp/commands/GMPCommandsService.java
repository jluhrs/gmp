package edu.gemini.aspen.gmp.commands;

import edu.gemini.aspen.gmp.commands.model.ActionManager;

//@Component
public class GMPCommandsService {
    private final ActionManager _actionManager = new ActionManager();

    //@Validate
    public void startService() {
        _actionManager.start();
    }

    //@Invalidate
    public void stopService() {
        _actionManager.stop();
    }
}
