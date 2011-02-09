package edu.gemini.aspen.gmp.pcs.model;

/**
 * Created by IntelliJ IDEA.
 * User: cquiroz
 * Date: 2/9/11
 * Time: 9:07 AM
 * To change this template use File | Settings | File Templates.
 */
public interface PcsUpdaterComposite extends PcsUpdater {
    /**
     * Register a new PcsUpdater in this aggregation
     * @param updater the new updater in the agregation
     */
    void registerUpdater(PcsUpdater updater);

    /**
     * Removes the given PcsUpdater from the aggregation
     * @param updater updater to remove
     */
    void unregisterUpdater(PcsUpdater updater);
}
