package edu.gemini.aspen.gmp.tcs.model;


import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Unit test for the TCS Context Simulator
 */

public class SimTcsContextFetcherTest {
    private static final double TOLERANCE = 0.0000001;
    private final double[] goodData = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

    /**
     * Test the case when a non existing data file is used
     */
    @Test(expected = RuntimeException.class)
    public void testNoFile() {
        new SimTcsContextFetcher(SimTcsContextFetcherTest.class.getResourceAsStream("UnexistingFile.data"));
    }

    /**
     * Test the case of using a file with invalid data
     */
    @Test
    public void testInvalidData() throws TcsContextException {
        TcsContextFetcher fetcher = new SimTcsContextFetcher(SimTcsContextFetcherTest.class.getResourceAsStream("tcsCtxInvalidData.data"));
        assertEquals(null, fetcher.getTcsContext());

    }

    /**
     * Test the case of using a file with some invalid data, but at least one
     * line with good data
     */
    @Test
    public void testSomeInvalidData() throws TcsContextException {
        TcsContextFetcher fetcher = new SimTcsContextFetcher(SimTcsContextFetcherTest.class.getResourceAsStream("tcsCtxSomeInvalidData.data"));

        double time = System.currentTimeMillis() / 1000.0;
        double[] tcsContextValues = fetcher.getTcsContext();

        assertEquals(time, tcsContextValues[0], 1); //tolerance is one second. Just to make sure the value is a time

        //reads the data, starting from element at position 1
        for (int i = 1; i < SimTcsContextFetcher.TCS_CTX_LENGTH; i++) {
            assertEquals(goodData[i - 1], tcsContextValues[i], TOLERANCE);
        }

        //if tried again, we should get the same values as before, except
        //(possibly) for the timestamp
        time = System.currentTimeMillis() / 1000.0;
        tcsContextValues = fetcher.getTcsContext();

        assertEquals(time, tcsContextValues[0], 1); //tolerance is one second. Just to make sure the value is a time

        //reads the data, starting from element at position 1
        for (int i = 1; i < SimTcsContextFetcher.TCS_CTX_LENGTH; i++) {
            assertEquals(goodData[i - 1], tcsContextValues[i], TOLERANCE);
        }
    }

    /**
     * Test the case when the data file exists but does not contain enough data
     */
    @Test
    public void testNotEnoughData() throws TcsContextException {
        TcsContextFetcher fetcher = new SimTcsContextFetcher(SimTcsContextFetcherTest.class.getResourceAsStream("tcsCtxNoData.data"));
        assertEquals(null, fetcher.getTcsContext());
    }

    /**
     * Test a normal case, reading the values from a data file. Checks
     * that the values are repeated once the data is over. Also
     * validates that the first element corresponds to a timestamp
     */
    @Test
    public void testDataRepeating() throws TcsContextException {
        TcsContextFetcher fetcher = new SimTcsContextFetcher(SimTcsContextFetcherTest.class.getResourceAsStream("tcsCtx.data"));

        for (int repetition = 0; repetition < 10; repetition++) {
            double time = System.currentTimeMillis() / 1000.0;
            double tcsContextValues[] = fetcher.getTcsContext();
            assertEquals(time, tcsContextValues[0], 1); //tolerance is one second. Just to make sure the value is a time

            //reads the data, starting from element at position 1
            for (int i = 1; i < SimTcsContextFetcher.TCS_CTX_LENGTH; i++) {
                assertEquals(goodData[i - 1], tcsContextValues[i], TOLERANCE);
            }

            //second line is just 1
            time = System.currentTimeMillis() / 1000.0;
            tcsContextValues = fetcher.getTcsContext();
            assertEquals(time, tcsContextValues[0], 1); //tolerance is one second. Just to make sure the value is a time

            for (int i = 1; i < SimTcsContextFetcher.TCS_CTX_LENGTH; i++) {
                assertEquals(1.0, tcsContextValues[i], TOLERANCE);
            }

            //third line is just 2
            time = System.currentTimeMillis() / 1000.0;
            tcsContextValues = fetcher.getTcsContext();
            assertEquals(time, tcsContextValues[0], 1); //tolerance is one second. Just to make sure the value is a time
            for (int i = 1; i < SimTcsContextFetcher.TCS_CTX_LENGTH; i++) {
                assertEquals(2.0, tcsContextValues[i], TOLERANCE);
            }
            //and then repeat, next line is a sequence, the following is a bunch of zeros, etc
        }
    }
}
