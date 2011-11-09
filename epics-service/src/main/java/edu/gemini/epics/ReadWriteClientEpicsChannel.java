package edu.gemini.epics;

import edu.gemini.epics.api.Channel;

/**
 * Interface ReadWriteClientEpicsChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 11/9/11
 */
public interface ReadWriteClientEpicsChannel<T> extends ReadOnlyClientEpicsChannel<T>, Channel<T> {
}
