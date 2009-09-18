package edu.gemini.aspen.gmp.pcs.model.updaters;

import edu.gemini.aspen.gmp.pcs.model.PcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdate;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;

import java.util.logging.Logger;

/**
 * A simple PcsUpdater implementation that just logs the requests
 */
public class LogPcsUpdater implements PcsUpdater {

    private static final Logger LOG = Logger.getLogger(LogPcsUpdater.class.getName());

    @Override
    public void update(PcsUpdate update) throws PcsUpdaterException {

        LOG.info("PCS Update received " + update);


    }
}
