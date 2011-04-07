package edu.gemini.epics.impl;

import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsWriter;
import edu.gemini.epics.JCAContextController;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.logging.Logger;

/**
 * Implementation of the EpicsWriter interface that allows to write values
 * to a previously bound value
 */
@Component
@Instantiate
@Provides(specifications = EpicsWriter.class)
public class EpicsWriterImpl extends EpicsBaseImpl implements EpicsWriter {
    private static final Logger LOG = Logger.getLogger(EpicsWriter.class.getName());

    public EpicsWriterImpl(@Requires JCAContextController epicsService) throws CAException {
        super(epicsService);
    }

    @Validate
    public void startEpicsWriter() {
        LOG.fine("EpicsWriter ready");
    }

    public void write(String channelName, Double value) throws EpicsException {
        Channel channel = getChannel(channelName);
        if (isChannelKnown(channelName)) {
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
        Channel channel = getChannel(channelName);
        if (isChannelKnown(channelName)) {
            try {
                channel.put(value);
                channel.getContext().flushIO();
            } catch (CAException e) {
                throw new EpicsException("Problem writing to channel " + channelName, e);
            }
        }
    }
}
