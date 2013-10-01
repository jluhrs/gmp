package edu.gemini.aspen.gmp.pcs.model.updaters;

import com.google.common.collect.ImmutableList;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdate;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.api.Channel;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of a PCS Updater object that will send zernikes updates to
 * the EPICS channels in the TCS.
 *
 * There are 14 zernikes to write.
 */
public class EpicsPcsUpdater implements PcsUpdater {
    private static final Logger LOG = Logger.getLogger(EpicsPcsUpdater.class.getName());
    public static final String TCS_ZERNIKES_BASE_CHANNEL = "tst:array";
    public static final int ARRAY_LENGTH = 40;
    public static final int MAX_ZERNIKES = 19;

    private final ChannelAccessServer _channelFactory;
    private final Channel<Double> zernikesChannel;
    private final Double[] gains;
    private int taiDiff;

    public EpicsPcsUpdater(ChannelAccessServer channelFactory, String baseChannel, List<Double> gains, int taiDiff) throws PcsUpdaterException {
        _channelFactory = channelFactory;
        this.taiDiff = taiDiff;
        Double coeff[] = new Double[ARRAY_LENGTH];
        gains.toArray(coeff);
        for (int i = 0; i < coeff.length; i++) {
            if (coeff[i] == null) {
                coeff[i] = new Double(1.0);
            }
        }
        this.gains = coeff;

        /**
         * If the baseChannel is not specified, use the default one
         */
        try {
            if (baseChannel == null) {
                zernikesChannel = _buildChannelList(TCS_ZERNIKES_BASE_CHANNEL);
                LOG.info("Using default epics baseChannel " + TCS_ZERNIKES_BASE_CHANNEL);
            } else {
                zernikesChannel = _buildChannelList(baseChannel);
            }
        } catch (CAException e) {
            throw new PcsUpdaterException("Exception creating EPICS channel", e);
        }
    }

    public void update(PcsUpdate update) throws PcsUpdaterException {
        if (update == null) {
            LOG.warning("PCS Update is null.");
            return;
        }
        //attempt to write the values to EPICS
        LOG.info("Updating PCS on channel " + zernikesChannel.getName() + " with " + update.getZernikes().length + " zernikes");
        try {

            Double[] zernikes = update.getZernikes();

            if (zernikes == null || zernikes.length == 0) {
                LOG.warning("No Zernikes available in this update");
                return;
            }
            long utc = System.currentTimeMillis();
            long tai = utc + taiDiff * 1000;

            Double[] exposedArray = new Double[ARRAY_LENGTH];
            // TAI on the first zernike
            exposedArray[0] = (double)tai;
            // Number of coefficients
            long ncoeff = Math.min(zernikes.length, MAX_ZERNIKES);
            exposedArray[1] = (double)ncoeff;
            for (int i = 2; i < ARRAY_LENGTH; i++) {
                if (i < zernikes.length + 2) {
                    exposedArray[i] = zernikes[i - 2] * gains[i - 2];
                } else {
                    exposedArray[i] = 0.0;
                }
            }
            zernikesChannel.setValue(Arrays.asList(exposedArray));
        } catch (EpicsException e) {
            throw new PcsUpdaterException("Trouble writing zernikes coefficients", e);
        } catch (TimeoutException e) {
            throw new PcsUpdaterException("Trouble writing zernikes coefficients", e);
        } catch (CAException e) {
            throw new PcsUpdaterException("Trouble writing zernikes coefficients", e);
        }
    }

    private Channel<Double> _buildChannelList(String baseChannel) throws CAException {
        LOG.info("Create channel for publishing zernikes " + baseChannel);
        return _channelFactory.createChannel(baseChannel, buildZeroZernikesArray());
    }

    public static ImmutableList<Double> buildZeroZernikesArray() {
        ImmutableList.Builder<Double> builder = new ImmutableList.Builder<Double>();
        for (int i = 0; i < ARRAY_LENGTH; i++) {
            builder.add(0.0);
        }
        return builder.build();
    }

    public void stopChannel() {
        LOG.info("Stopping channel for PCS Updatus: " + zernikesChannel.getName());
        _channelFactory.destroyChannel(zernikesChannel);
    }
}
