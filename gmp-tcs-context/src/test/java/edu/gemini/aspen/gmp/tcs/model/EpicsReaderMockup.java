package edu.gemini.aspen.gmp.tcs.model;

import edu.gemini.epics.IEpicsReader;
import edu.gemini.epics.EpicsException;

/**
 * A mockup Epics Reader for testing
 */
public class EpicsReaderMockup implements IEpicsReader {

    String _channel;

    Object _context;

    public Object getValue(String channelName) throws EpicsException {
        return _context;
    }


    public void bindChannel(String channel) throws EpicsException {
        _channel = channel;
    }

    public String getBindedChannel() {
        return _channel;
    }

    public void setContext(double[] ctx) {
        _context = new double[ctx.length];
        System.arraycopy(ctx, 0, _context, 0, ctx.length);
    }

    public void setValue(Object o) {
        _context = o;
    }
}
