package edu.gemini.aspen.gmp.tcs.model;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


/**
 * Test class for the TCS Context Fetcher Interface
 */
public class EpicsTcsContextFetcherTest {
    private static final double ERROR_TOLERANCE = 0.000000001;
    private EpicsReaderMock _epicsReaderMock;
    private TcsContextFetcher _fetcher;

    @Test
    public void testUseDefaultChannel() throws TcsContextException {
        _epicsReaderMock = new EpicsReaderMock("channel", null);
        _fetcher = new EpicsTcsContextFetcher(_epicsReaderMock);
        assertEquals(EpicsTcsContextFetcher.TCS_CONTEXT_CHANNEL, _epicsReaderMock.getBoundChannel());
    }

    @Test
    public void testUseNonDefaultChannel() throws TcsContextException {
        _epicsReaderMock = new EpicsReaderMock("channel", null);
        _fetcher = new EpicsTcsContextFetcher(_epicsReaderMock, "otherChannel");
        assertEquals("otherChannel", _epicsReaderMock.getBoundChannel());
    }

    @Test(expected = TcsContextException.class)
    public void testInvalidReaderValue() throws TcsContextException {
        _epicsReaderMock = new EpicsReaderMock("channel", "Not A Double Array");
        _fetcher = new EpicsTcsContextFetcher(_epicsReaderMock);
        _fetcher.getTcsContext();
    }

    @Test
    public void testNullChannel() throws TcsContextException {
        _epicsReaderMock = new EpicsReaderMock("channel", null);
        _fetcher = new EpicsTcsContextFetcher(_epicsReaderMock);
        assertArrayEquals(new double[0], _fetcher.getTcsContext(), ERROR_TOLERANCE);
    }

    @Test
    public void testNullChannelOnConstructor() throws TcsContextException {
        _epicsReaderMock = new EpicsReaderMock("channel", null);
        _fetcher = new EpicsTcsContextFetcher(_epicsReaderMock, null);
        assertArrayEquals(new double[0], _fetcher.getTcsContext(), ERROR_TOLERANCE);
    }

    @Test
    public void testGetContext() throws TcsContextException {
        double simulatedValues[] = new double[39];
        for (int i = 0; i < simulatedValues.length; i++) {
            simulatedValues[i] = (double) i;
        }

        _epicsReaderMock = new EpicsReaderMock("channel", simulatedValues);
        _fetcher = new EpicsTcsContextFetcher(_epicsReaderMock);
        double[] valuesFromContext = _fetcher.getTcsContext();

        assertArrayEquals(simulatedValues, valuesFromContext, ERROR_TOLERANCE);
    }
}
