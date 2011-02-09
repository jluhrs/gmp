package edu.gemini.aspen.gmp.pcs.model;

/**
 * Created by IntelliJ IDEA.
 * User: cquiroz
 * Date: 2/9/11
 * Time: 9:07 AM
 * To change this template use File | Settings | File Templates.
 */
public interface PcsUpdaterComposite extends PcsUpdater {
    void registerUpdater(PcsUpdater updater);

    void unregisterUpdater(PcsUpdater updater);
}
