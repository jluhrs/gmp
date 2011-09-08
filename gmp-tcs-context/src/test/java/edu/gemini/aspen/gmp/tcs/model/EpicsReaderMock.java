package edu.gemini.aspen.gmp.tcs.model;

import edu.gemini.epics.EpicsReader;
import edu.gemini.epics.EpicsException;
import gov.aps.jca.event.ConnectionListener;
import org.apache.commons.lang.NotImplementedException;

/**
 * A mockup Epics Reader for testing
 */
public class EpicsReaderMock implements EpicsReader {
    private String _channel;

    @Override
    public void bindChannelAsync(String channel) throws EpicsException {
        throw new NotImplementedException();
    }

    @Override
    public void bindChannelAsync(String channel, ConnectionListener listener) throws EpicsException {
        throw new NotImplementedException();
    }

    @Override
    public boolean isChannelConnected(String channel) {
        throw new NotImplementedException();
    }

    private final Object _context;

    public EpicsReaderMock(String _channel, Object _context) {
        this._channel = _channel;
        this._context = _context;
    }

    public Object getValue(String channelName) throws EpicsException {
        return _context;
    }

    public String getBoundChannel() {
        return _channel;
    }

    @Override
    public void bindChannel(String channel) throws EpicsException {
        this._channel = channel;
    }

    @Override
    public void unbindChannel(String channel) throws EpicsException {
        this._channel = null;
    }

    @Override
    public String toString() {
        return "EpicsReader{" +
                "channel='" + _channel + '\'' +
                '}';
    }
}
