package edu.gemini.aspen.gmp.tcs.model;

/**
 * A simple interface to obtain the TCS Context as an array of
 * double values.
 */
public interface TcsContextFetcher {

    /**
     * Get the TCS Context as an array of doubles
     *
     * @return an array of double values with the TCS context, or
     *         <code>an empty array</code> if the context cannot be read
     * @throws TcsContextException in case there is an exception
     *                             trying to get the TCS Context.
     */
    double[] getTcsContext() throws TcsContextException;
}
