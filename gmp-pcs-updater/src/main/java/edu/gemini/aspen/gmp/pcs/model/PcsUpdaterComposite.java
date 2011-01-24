package edu.gemini.aspen.gmp.pcs.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Interface to define a composite of several PCS updater objects
 */
public class PcsUpdaterComposite implements PcsUpdater {

    private List<PcsUpdater> _pcsUpdaters;

    /**
     * Initialize the composite.
     */
    public PcsUpdaterComposite() {
        _pcsUpdaters = new CopyOnWriteArrayList<PcsUpdater>();
    }
    /**
     * Register a new PcsUpdater in this aggregation
     * @param updater the new updater in the agregation
     */
    public void registerUpdater(PcsUpdater updater) {
        _pcsUpdaters.add(updater);
    }

    /**
     * Removes the given PcsUpdater from the aggregation
     * @param updater updater to remove
     */
    public void unregisterUpdater(PcsUpdater updater) {
        _pcsUpdaters.remove(updater);
    }

    public void update(PcsUpdate update) throws PcsUpdaterException {
        for (PcsUpdater updater: _pcsUpdaters) {
            updater.update(update);
        }
    }
}
