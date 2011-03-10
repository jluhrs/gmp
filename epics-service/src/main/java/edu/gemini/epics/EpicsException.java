package edu.gemini.epics;

/**
 * Exception produced when accessing the EPICS layer
 */
public class EpicsException extends RuntimeException {

    public EpicsException(String message, Exception cause) {
        super(message, cause);
    }

    public EpicsException(String message) {
        super(message);
    }

}
