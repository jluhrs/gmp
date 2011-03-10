package edu.gemini.aspen.gmp.commands;

import edu.gemini.aspen.gmp.commands.model.ActionManager;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;

//@Component
public class GMPCommandsService {
    private final ActionManager _actionManager = new ActionManager();

    @Validate
    public void startService() {
        _actionManager.start();
    }

    @Invalidate
    public void stopService() {
        _actionManager.stop();
    }
}
