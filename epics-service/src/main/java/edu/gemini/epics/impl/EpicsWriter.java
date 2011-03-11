package edu.gemini.epics.impl;

import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.IEpicsWriter;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.logging.Logger;

/**
 * Implementation of the EpicsWriter interface using JCA
 */
@Component
@Instantiate
@Provides(specifications = IEpicsWriter.class)
public class EpicsWriter extends EpicsBase implements IEpicsWriter {
    private static final Logger LOG = Logger.getLogger(EpicsWriter.class.getName());

    public EpicsWriter(@Requires EpicsService epicsService) throws CAException {
        super(epicsService);
        startEpicsWriter();
    }

    @Validate
    public void startEpicsWriter() {
        LOG.info("EpicsWriter ready" );
    }

    public void write(String channelName, Double value) throws EpicsException {
        if (isChannelKnown(channelName)) {
            Channel channel = getChannel(channelName);
            try {
                channel.put(value);
                channel.getContext().flushIO();
            } catch (CAException e) {
                throw new EpicsException("Problem writing to channel " + channelName, e);
            }
        }
    }

    public void write(String channelName, Double[] value) throws EpicsException {
        double val[] = new double[value.length];
        for (int i = 0; i < val.length; i++) {
            val[i] = value[i];
        }
        write(channelName, val);
    }

    public void write(String channelName, double[] value) throws EpicsException {
        if (isChannelKnown(channelName)) {
            Channel channel = getChannel(channelName);

            try {
                channel.put(value);
                channel.getContext().flushIO();
            } catch (CAException e) {
                throw new EpicsException("Problem writing to channel " + channelName, e);
            }
        }
    }
}
