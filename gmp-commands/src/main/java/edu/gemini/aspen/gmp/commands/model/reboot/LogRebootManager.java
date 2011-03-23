package edu.gemini.aspen.gmp.commands.model.reboot;

import edu.gemini.aspen.giapi.commands.RebootArgument;
import edu.gemini.aspen.gmp.commands.model.RebootManager;

import java.util.logging.Logger;

/**
 * This class just prints a Log message that the reboot sequence command
 * will be executed.
 */
public class LogRebootManager implements RebootManager {
    private static final Logger LOG = Logger.getLogger(LogRebootManager.class.getName());

    @Override
    public void reboot(RebootArgument arg) {
        LOG.info("Starting shutdown of the instrument with argument " + arg);
    }

}
