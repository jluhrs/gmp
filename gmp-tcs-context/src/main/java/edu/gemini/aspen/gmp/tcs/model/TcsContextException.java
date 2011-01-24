package edu.gemini.aspen.gmp.tcs.model;

/**
 * TCS Context exception
 */
public class TcsContextException extends Exception {

    public TcsContextException(String message, Exception cause) {
        super(message, cause);
    }

    public TcsContextException(String message) {
        super(message);
    }

}
