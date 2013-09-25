package edu.gemini.aspen.gmp.pcs.model.updaters;

import edu.gemini.aspen.gmp.pcs.model.PcsUpdate;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelFactory;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsWriter;
import edu.gemini.epics.ReadWriteClientEpicsChannel;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of a PCS Updater object that will send zernikes updates to
 * the EPICS channels in the TCS.
 *
 *
 *
 * There are 14 zernikes to write.
 */
public class EpicsPcsUpdater implements PcsUpdater {
    private static final Logger LOG = Logger.getLogger(EpicsPcsUpdater.class.getName());
    static final String TCS_ZERNIKES_BASE_CHANNEL = "tst:array";
    public static final String[] INPUTS = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"};

    private final EpicsWriter _writer;
    private final String[] _channels;
    private final ChannelAccessServer _channelFactory;
    private List<ReadWriteClientEpicsChannel<Double>> epicsChannels = new ArrayList<ReadWriteClientEpicsChannel<Double>>();

    public EpicsPcsUpdater(ChannelAccessServer channelFactory, EpicsWriter writer, String baseChannel) throws PcsUpdaterException {
        _writer = writer;
        _channels = new String[INPUTS.length];
        _channelFactory = channelFactory;
        /**
         * If the baseChannel is not specified, use the default one
         */
        if (baseChannel == null) {
            LOG.info("Using default epics baseChannel " + TCS_ZERNIKES_BASE_CHANNEL);
            _buildChannelList(TCS_ZERNIKES_BASE_CHANNEL).toArray(_channels);
        } else {
            _buildChannelList(baseChannel).toArray(_channels);
        }
        bindEpicsChannels();
    }

    private void bindEpicsChannels() throws PcsUpdaterException {
        for (String channel : _channels) {
            try {
                epicsChannels.add(_writer.getDoubleChannel(channel));
            } catch (EpicsException e) {
                throw new PcsUpdaterException("Problem binding " +
                        channel +
                        " channel. Check the EPICS configuration and your network settings", e);
            }
        }
    }

    public void update(PcsUpdate update) throws PcsUpdaterException {
        if (update == null) {
            LOG.warning("PCS Update is null.");
            return;
        }
        //attempt to write the values to EPICS
        LOG.info("Updating PCS on channels " + epicsChannels);
        try {

            Double[] zernikes = update.getZernikes();

            if (zernikes == null || zernikes.length == 0) {
                LOG.warning("No Zernikes available in this update");
                return;
            }

            for (int i = 0; i < zernikes.length && i < INPUTS.length; i++) {
                LOG.fine("Zernike update (" + zernikes[i] + ") to channel " + _channels[i]);
                epicsChannels.get(i).setValue(zernikes[i]);
            }

        } catch (EpicsException e) {
            throw new PcsUpdaterException("Trouble writing zernikes coefficients", e);
        } catch (TimeoutException e) {
            throw new PcsUpdaterException("Trouble writing zernikes coefficients", e);
        } catch (CAException e) {
            throw new PcsUpdaterException("Trouble writing zernikes coefficients", e);
        }
    }

    private List<String> _buildChannelList(String baseChannel) {
        try {
            System.out.println("Try to create channel " + baseChannel);
            _channelFactory.createChannel(baseChannel, new ArrayList<Double>(INPUTS.length));
        } catch (CAException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        List<String> channels = new ArrayList<String>(INPUTS.length);
        baseChannel += ".";
        for (String channel : INPUTS) {
            channels.add(baseChannel + channel);
        }
        return channels;
    }
}
