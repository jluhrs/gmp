package edu.gemini.aspen.gmp.epics;

import java.util.Set;

/**
 * An Epics Configuration contains information about
 * the valid epics channels that can be monitored
 * through the Gemini Master Process.
 * <br>
 * The Epics Configuration information is collected
 * at startup by the GMP through the Epics Access bundle.
 */
public interface EpicsConfiguration {

    /**
     * Get a set of all the valid epics channels that
     * can be monitored via the GMP. A valid epics channel
     * is one that instrument code can monitor for value changes.
     * @return a set of valid epics channel names.
     */
    Set<String> getValidChannelsNames();

}
