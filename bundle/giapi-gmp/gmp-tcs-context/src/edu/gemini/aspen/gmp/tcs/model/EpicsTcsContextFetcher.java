package edu.gemini.aspen.gmp.tcs.model;

import edu.gemini.epics.IEpicsReader;
import edu.gemini.epics.EpicsException;

/**
 * The Epics Tcs Context Fetcher provides a mechanism to obtain the
 * TCS Context from Gemini via EPICS channel.
 */
public class EpicsTcsContextFetcher implements TcsContextFetcher {

    /**
     * Default channel to get the TCS Context from Gemini
     */
    public static final String TCS_CONTEXT_CHANNEL = "tcs:sad:astCtx";

    /**
     * EPICS Reader instance
     */
    private IEpicsReader _reader;

    /**
     * Actual channel name to obtain the TCS Context from Gemini
     */
    private String _tcsCtxChannel;

    /**
     * Constructor. Takes as an argument the EPICS reader to get access
     * to EPICS, and the TCS Context channel to use. If the TCS Context
     * channel is <code>null</code> the default TCS_CONTEXT_CHANNEL is
     * used.
     *
     * @param reader        The EPICS reader
     * @param tcsCtxChannel the channel containing the TCS Context
     * @throws TcsContextException in case there is a problem obtaining
     *                             the TCS Context
     */
    public EpicsTcsContextFetcher(IEpicsReader reader, String tcsCtxChannel)
            throws TcsContextException {
        _reader = reader;

        _tcsCtxChannel = tcsCtxChannel;
        /**
         * If no context channel is passed, then we will use the default value
         */
        if (_tcsCtxChannel == null)
            _tcsCtxChannel = TCS_CONTEXT_CHANNEL;

        try {
            _reader.bindChannel(_tcsCtxChannel);
        } catch (EpicsException e) {
            throw new TcsContextException("Problem binding " +
                    TCS_CONTEXT_CHANNEL +
                    " channel. Check the EPICS configuration and your network settings", e);
        }
    }

    /**
     * Constructor. Use the specified EPICS reader to get the TCS Context.
     * The TCS Context is obtained from the default channel, specified in
     * TCS_CONTEXT_CHANNEL
     *
     * @param reader EPICS reader
     * @throws TcsContextException in case of problems obtaininig the TCS
     */
    public EpicsTcsContextFetcher(IEpicsReader reader) throws TcsContextException {
        this(reader, null);
    }


    /**
     * Get the TCS Context as an array of doubles
     *
     * @return an array of double values with the TCS context, or
     *         <code>null</code> if the context cannot be read
     * @throws TcsContextException in case there is an exception
     *                             trying to get the TCS Context.
     */
    public double[] getTcsContext() throws TcsContextException {

        try {
            Object o = _reader.getValue(_tcsCtxChannel);

            if (o == null) return null;

            if (!(o instanceof double[])) {
                throw new TcsContextException("Invalid data obtained from EPICS channel " + _tcsCtxChannel + ": " + o);
            }

            return (double[]) o;

        } catch (EpicsException e) {
            throw new TcsContextException("Problem getting the TCS Context from EPICS channel " + _tcsCtxChannel, e);
        }

    }
}
