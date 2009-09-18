package edu.gemini.aspen.gmp.pcs.test;

import edu.gemini.aspen.gmp.pcs.model.PcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdate;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;

/**
 * A test implementation of the PcsUpdater class.
 */
public final class TestPcsUpdater implements PcsUpdater {

    private PcsUpdate lastUpdate = null;

    public void reset() {
        lastUpdate = null;
    }

    @Override
    public void update(PcsUpdate update) throws PcsUpdaterException {
        synchronized (this) {
            lastUpdate = update;
            notifyAll();
        }
    }

    public PcsUpdate getUpdate() {
        return lastUpdate;
    }
}