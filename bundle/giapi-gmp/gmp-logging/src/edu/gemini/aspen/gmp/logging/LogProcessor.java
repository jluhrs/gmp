package edu.gemini.aspen.gmp.logging;

/**
 * A LogProcessor interfaces provides an abstract method to process
 * the logging messages coming from the instrument.
 *
 * The implementations of this class could do simple operations like
 * logging the messages using Java logging libraries, or more complex
 * operations like sending the logging information to a centralized
 * repository, if it's available.
 */
public interface LogProcessor {

    /**
     * Process (or digest) the given LogMessage
     * @param msg the log message received by the GMP
     */
    void processLogMessage(LogMessage msg);

}
