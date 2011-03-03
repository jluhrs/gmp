package edu.gemini.aspen.giapitestsupport;

/**
 */
public class TesterException extends Exception {

    public TesterException(String msg) {
        super(msg);
    }

    public TesterException() {
        super();
    }

    public TesterException(Exception e) {
        super(e);
    }

}
