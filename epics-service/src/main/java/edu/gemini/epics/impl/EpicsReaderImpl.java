package edu.gemini.epics.impl;

import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsReader;
import edu.gemini.epics.JCAContextController;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBR;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.logging.Logger;


/**
 * An Epics Reader object, that allows to get the value of a
 * bound Epics Channel.
 */
@Component
@Instantiate
@Provides(specifications = EpicsReader.class)
public class EpicsReaderImpl extends EpicsBaseImpl implements EpicsReader {
    private static final Logger LOG = Logger.getLogger(EpicsReader.class.getName());
    private static final double[] EMPTY_CHANNEL_VALUE = new double[0];

    public EpicsReaderImpl(@Requires JCAContextController epicsService) {
        super(epicsService);
    }

    /**
     * Reads the value from the EPICS channel, and returns it as
     * an Object.
     *
     * @param channelName EPICS channel to read from
     * @return Object containing the value in the EPICS channel.
     * @throws EpicsException
     */
    public Object getValue(String channelName) throws EpicsException {
        Channel channel = getChannel(channelName);
        if (isChannelKnown(channelName)) {
            return readChannelValue(channel);
        } else {
            return EMPTY_CHANNEL_VALUE;
        }
    }

    @Validate
    public void startEpicsReader() {
        LOG.info("EpicsReader ready" );
    }

    private Object readChannelValue(Channel channel) {
        try {
            return readEpicsValue(channel);
        } catch (CAException e) {
            throw new EpicsException("Problem reading channel " + channel, e);
        } catch (TimeoutException e) {
            throw new EpicsException("Timeout while reading channel " + channel, e);
        }
    }

    private Object readEpicsValue(Channel channel) throws CAException, TimeoutException {
        // There is a slight chance of problems here as the channel could be unbound anytimes
        DBR dbr = channel.get();
        channel.getContext().pendIO(1.0);
        return dbr.getValue();
    }
}
