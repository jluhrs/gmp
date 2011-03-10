package edu.gemini.epics.impl;

import edu.gemini.epics.EpicsException;
import edu.gemini.epics.IEpicsReader;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBR;


/**
 * An Epics Reader object, that allows to get the value of a
 * bound Epics Channel.
 */
public class EpicsReader extends EpicsBase implements IEpicsReader {
    public EpicsReader(Context ctx) throws CAException {
        super(ctx);
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
        if (channel == null) {
            return null;
        }

        try {
            return readEpicsValue(channel);
        } catch (CAException e) {
            throw new EpicsException("Problem reading channel " + channel, e);
        } catch (TimeoutException e) {
            throw new EpicsException("Timeout while reading channel " + channel, e);
        }
    }

    private Object readEpicsValue(Channel channel) throws CAException, TimeoutException {
        DBR dbr = channel.get();
        channel.getContext().pendIO(1.0);
        return dbr.getValue();
    }

}
