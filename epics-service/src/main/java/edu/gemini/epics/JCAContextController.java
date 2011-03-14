package edu.gemini.epics;

import gov.aps.jca.Context;

/**
 * An JCAContextController holds the control of JCAContext taking care of building and disposing it
 */
public interface JCAContextController {
    /**
     * Returns a valid JCA context
     *
     * @return a valid JCA Context
     * @throws IllegalStateException In case the Context is not available
     */
    Context getJCAContext() throws IllegalStateException;
}
