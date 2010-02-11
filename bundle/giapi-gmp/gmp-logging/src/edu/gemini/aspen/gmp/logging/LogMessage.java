package edu.gemini.aspen.gmp.logging;

/**
 * A logging message interface
 */
public interface LogMessage {

    /**
     * The severity of this logging information. This is used
     * to determine the importance of this message.
     * @return the Severity of this log message
     */
    Severity getSeverity();

    /**
     * The log message itself
     * @return string with the log message itself.
     */
    String getMessage();
    
}
