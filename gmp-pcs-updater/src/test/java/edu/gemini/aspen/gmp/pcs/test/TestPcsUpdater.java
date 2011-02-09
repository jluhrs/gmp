package edu.gemini.aspen.gmp.pcs.test;

import edu.gemini.aspen.gmp.pcs.model.PcsUpdate;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterComposite;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;
import org.junit.Ignore;

/**
 * A test implementation of the PcsUpdater class.
 */
@Ignore
public final class TestPcsUpdater implements PcsUpdaterComposite {

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

    @Override
    public void registerUpdater(PcsUpdater updater) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unregisterUpdater(PcsUpdater updater) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}