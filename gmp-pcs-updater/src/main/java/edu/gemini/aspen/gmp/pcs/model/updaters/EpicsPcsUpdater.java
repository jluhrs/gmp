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
    private static final String TCS_ZERNIKES_CHANNEL = "tst:array.VALJ";

    private final IEpicsWriter _writer;
    private final String _channel;

    public EpicsPcsUpdater(IEpicsWriter writer, String channel) throws PcsUpdaterException {
        _writer = writer;

        /**
         * If the channel is not specified, use the default one
         */
        if (channel == null) {
            LOG.info("Using default epics channel " + TCS_ZERNIKES_CHANNEL);
            _channel = TCS_ZERNIKES_CHANNEL;
        } else {
            _channel = channel;
        }
        bindEpicsChannel();
    }

    private void bindEpicsChannel() throws PcsUpdaterException {
        try {
            _writer.bindChannel(_channel);
        } catch (EpicsException e) {
            throw new PcsUpdaterException("Problem binding " +
                                   _channel +
                                   " channel. Check the EPICS configuration and your network settings", e);
        }
    }

    public void update(PcsUpdate update) throws PcsUpdaterException {
        if (update == null) {
            LOG.warning("PCS Update is null");
            return;
        }
        //attempt to write the values to EPICS
        try {
            LOG.fine("Post Zernikes updates to EPICS Channel: " + _channel);
            _writer.write(_channel, update.getZernikes());
        } catch (EpicsException e) {
            throw new PcsUpdaterException("Trouble writing zernikes coefficients", e);
        }
    }
}
