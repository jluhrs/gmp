package edu.gemini.aspen.gmp.tcs.model;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Test class for the TCS Context Fetcher Interface
 */
public class EpicsTcsContextFetcherTest {


    EpicsReaderMockup _epicsReaderMockup;

    TcsContextFetcher _fetcher;

    @Before
    public void setUp() {
        _epicsReaderMockup = new EpicsReaderMockup();

    }

    @Test
    public void testUseDefaultChannel() {
        try {
            _fetcher = new EpicsTcsContextFetcher(_epicsReaderMockup);
            assertEquals(EpicsTcsContextFetcher.TCS_CONTEXT_CHANNEL, _epicsReaderMockup.getBindedChannel());
        } catch (TcsContextException e) {
            fail("Unexpected exception" + e);
        }
    }

    @Test
    public void testUseNonDefaultChannel() {
        try {
            _fetcher = new EpicsTcsContextFetcher(_epicsReaderMockup, "otherChannel");
            assertEquals("otherChannel", _epicsReaderMockup.getBindedChannel());
        } catch (TcsContextException e) {
            fail("Unexpected exception" + e);
        }
    }

    @Test
    (expected = TcsContextException.class)
    public void testInvalidTcsContext() throws TcsContextException {

        _epicsReaderMockup.setValue("Not A Double Array");
        _fetcher = new EpicsTcsContextFetcher(_epicsReaderMockup);
        _fetcher.getTcsContext();
    }

    @Test
    public void testNullTcsContext() throws TcsContextException {

        _epicsReaderMockup.setValue(null);
        _fetcher = new EpicsTcsContextFetcher(_epicsReaderMockup);
        assertEquals(_fetcher.getTcsContext(), null);
    }



    @Test
    public void testGetContext() {

        double x[] = new double[39];

        for (int i = 0; i < 39; i++) {
            x[i] = (double) i;
        }

        _epicsReaderMockup.setContext(x);

        try {
            _fetcher = new EpicsTcsContextFetcher(_epicsReaderMockup);
            double[] ctx = _fetcher.getTcsContext();

            for (int i = 0; i < x.length; i++) {
                assertEquals(x[i], ctx[i], 0.000000001);
            }


        } catch (TcsContextException e) {
            fail("Unexpected exception" + e);
        }
    }


    @After
    public void tearDown() {
        _epicsReaderMockup = null;
    }


}
