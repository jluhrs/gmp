package edu.gemini.epics;

import edu.gemini.epics.api.ReadOnlyChannel;
import gov.aps.jca.CAException;

/**
 * Interface ReadOnlyChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 11/4/11
 */
public interface ReadOnlyClientEpicsChannel<T> extends ReadOnlyChannel<T> {
    void destroy() throws CAException;
}
