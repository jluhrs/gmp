package edu.gemini.aspen.gmp.pcs.model.updaters;

import edu.gemini.epics.IEpicsWriter;
import edu.gemini.epics.EpicsException;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdate;

import java.util.logging.Logger;

/**
 * Implementation of a PCS Updater object that will send updates to
 * an EPICS channel. This is temporary now for testing; the real channels
 * to update Gemini will be obtained later, and probably will come from
 * a configuration file.
 */
public class EpicsPcsUpdater implements PcsUpdater {

    private static final Logger LOG = Logger.getLogger(EpicsPcsUpdater.class.getName());

    private final IEpicsWriter _writter;

    private static final String TCS_ZERNIZES_CHANNEL = "tst:array.VALJ";

    public EpicsPcsUpdater(IEpicsWriter writter) throws PcsUpdaterException {
        _writter = writter;
        try {
            _writter.bindChannel(TCS_ZERNIZES_CHANNEL);
        } catch (EpicsException e) {
            throw new PcsUpdaterException("Problem binding " +
                                   TCS_ZERNIZES_CHANNEL +
                                   " channel. Check the EPICS configuration and your network settings", e);
        }
    }

    @Override
    public void update(PcsUpdate update) throws PcsUpdaterException {

        if (update == null) {
            LOG.warning("PCS Update is null");
            return;
        }
        //attempt to write the values to EPICS
        try {
            LOG.fine("Post Zernikes updates to EPICS Channel: " + TCS_ZERNIZES_CHANNEL);
            _writter.write(TCS_ZERNIZES_CHANNEL, update.getZernikes());
        } catch (EpicsException e) {
            throw new PcsUpdaterException("Trouble writting zernikes coefficients", e);
        }
    }
}
