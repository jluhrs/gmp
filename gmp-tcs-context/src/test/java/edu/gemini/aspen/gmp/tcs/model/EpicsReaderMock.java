package edu.gemini.aspen.gmp.tcs.model;

import edu.gemini.epics.IEpicsReader;
import edu.gemini.epics.EpicsException;

/**
 * A mockup Epics Reader for testing
 */
public class EpicsReaderMock implements IEpicsReader {
    private String _channel;
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
    public String toString() {
        return "EpicsReader{" +
                "channel='" + _channel + '\'' +
                '}';
    }
}
