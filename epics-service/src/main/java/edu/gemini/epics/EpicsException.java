package edu.gemini.epics;

/**
 * Exception produced when accessing the EPICS layer
 * This normally wraps an underlying exception
 */
public class EpicsException extends RuntimeException {

    public EpicsException(String message, Exception cause) {
        super(message, cause);
    }

}
