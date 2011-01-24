package edu.gemini.aspen.gmp.pcs.test;

import edu.gemini.aspen.gmp.pcs.model.PcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdate;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;
import org.junit.Ignore;

/**
 * A test implementation of the PcsUpdater class.
 */
@Ignore
public final class TestPcsUpdater implements PcsUpdater {

    private PcsUpdate lastUpdate = null;

    public void reset() {
        lastUpdate = null;
    }

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