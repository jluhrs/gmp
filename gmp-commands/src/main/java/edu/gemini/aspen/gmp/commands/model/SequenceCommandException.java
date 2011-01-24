package edu.gemini.aspen.gmp.commands.model;

/**
 * Exception associated to operations with Sequence Commands.
 */
public class SequenceCommandException extends RuntimeException {
    public SequenceCommandException(String message, Exception cause) {
        super(message, cause);
    }

    public SequenceCommandException(String message) {
        super(message);
    }

}
