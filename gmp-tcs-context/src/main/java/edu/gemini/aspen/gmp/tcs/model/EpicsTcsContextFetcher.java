package edu.gemini.aspen.gmp.tcs.model;

import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsReader;
import edu.gemini.epics.ReadOnlyClientEpicsChannel;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBR;

/**
 * The Epics Tcs Context Fetcher provides a mechanism to obtain the
 * TCS Context from Gemini via EPICS channel.
 */
class EpicsTcsContextFetcher implements TcsContextFetcher {

    /**
     * Default channel to get the TCS Context from Gemini
     */
    public static final String TCS_CONTEXT_CHANNEL = "tcs:sad:astCtx";

    /**
     * EPICS Reader instance
     */
    private final EpicsReader _reader;

    /**
     * Actual channel name to obtain the TCS Context from Gemini
     */
    private final String _tcsCtxChannel;

    /**
     * The reference to the EPICS channel
     */
    private final ReadOnlyClientEpicsChannel<Double> tcsChannel;

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
    protected EpicsTcsContextFetcher(EpicsReader reader, String tcsCtxChannel)
            throws TcsContextException {
        _reader = reader;

        /**
         * If no context channel is passed, then we will use the default value
         */
        if (tcsCtxChannel == null) {
            _tcsCtxChannel = TCS_CONTEXT_CHANNEL;
        } else {
            _tcsCtxChannel = tcsCtxChannel;
        }

        try {
            tcsChannel = _reader.getDoubleChannel(_tcsCtxChannel);
        } catch (EpicsException e) {
            throw new TcsContextException("Problem binding " +
                    _tcsCtxChannel +
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
    protected EpicsTcsContextFetcher(EpicsReader reader) throws TcsContextException {
        this(reader, TCS_CONTEXT_CHANNEL);
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
            DBR dbr = tcsChannel.getDBR();

            if (dbr == null) {
                return new double[0];
            }

            Object readValue = dbr.getValue();

            if (readValue == null) {
                return new double[0];
            }

            if (!(readValue instanceof double[])) {
                throw new TcsContextException("Invalid data obtained from EPICS channel " + _tcsCtxChannel + ": " + readValue);
            }
            return (double[]) readValue;
        } catch (EpicsException e) {
            throw new TcsContextException("Problem getting the TCS Context from EPICS channel " + _tcsCtxChannel, e);
        } catch (TimeoutException e) {
            throw new TcsContextException("Problem getting the TCS Context from EPICS channel " + _tcsCtxChannel, e);
        } catch (CAException e) {
            throw new TcsContextException("Problem getting the TCS Context from EPICS channel " + _tcsCtxChannel, e);
        }
    }
}
