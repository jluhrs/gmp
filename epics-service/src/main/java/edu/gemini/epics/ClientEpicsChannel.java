package edu.gemini.epics;

import edu.gemini.epics.api.ReadOnlyChannel;

/**
 * Interface ReadOnlyChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 11/4/11
 */
public interface ClientEpicsChannel<T> extends ReadOnlyChannel<T> {
    void destroy();
}
