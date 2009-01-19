package edu.gemini.aspen.gmp.statusdb;

import edu.gemini.aspen.gmp.status.api.StatusHandler;
import edu.gemini.aspen.gmp.status.api.StatusItem;

import java.util.logging.Logger;

/**
 *
 */
public class StatusDatabase implements StatusHandler {

    private static final Logger LOG = Logger.getLogger(StatusDatabase.class.getName());

    public String getName() {
        return "Status Database";  
    }

    public void update(StatusItem item) {
        LOG.info("Status Item received: " + item);
    }

    public void start() {
        LOG.info("Starting up Status Database");
    }

    public void shutdown() {
        LOG.info("Shutting down Status Database");
    }


    @Override
    public String toString() {
        return getName();
    }
}
