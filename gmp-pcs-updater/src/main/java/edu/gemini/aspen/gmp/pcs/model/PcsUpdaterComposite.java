package edu.gemini.aspen.gmp.pcs.model;

/**
 * Composite of PcsUpdaters that distributes updates to a list of them
 */
public interface PcsUpdaterComposite extends PcsUpdater {
    void registerUpdater(PcsUpdater updater);

    void unregisterUpdater(PcsUpdater updater);
}
