package edu.gemini.cas;

import edu.gemini.epics.api.Channel;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;

import java.util.List;

/**
 * Interface ServerChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 11/9/11
 */
public interface ServerChannel<T> extends Channel<T> {
    @Override
    void setValue(T value) throws CAException;

    @Override
    void setValue(List<T> values) throws CAException;
}
