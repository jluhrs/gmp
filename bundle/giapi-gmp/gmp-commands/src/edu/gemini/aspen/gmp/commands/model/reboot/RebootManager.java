package edu.gemini.aspen.gmp.commands.model.reboot;

import edu.gemini.aspen.gmp.commands.api.RebootArgument;

import java.util.logging.Logger;

/**
 * This class is in charge of rebooting the system with the given arguments.
 *
 */
public class RebootManager {

    private static final Logger LOG = Logger.getLogger(RebootManager.class.getName());

    public void reboot(RebootArgument arg) {
        LOG.info("Starting shutdown of the instrument with argument " + arg);
        //it's a stub for now, but it will invoke the init.d script with
        //the arguments to perform a system shutdown. 
    }

}
