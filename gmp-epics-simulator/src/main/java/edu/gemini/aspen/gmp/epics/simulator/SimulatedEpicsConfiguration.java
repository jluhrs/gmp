package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.simulator.channels.SimulatedEpicsChannel;

import java.util.Set;

/**
 * A configuration of simulated EPICS channels simply
 * allows to retrieve the description of all the
 * EPICS channels that will be simultated by the GMP.
 *
 */
public interface SimulatedEpicsConfiguration {
    
    static final String CHANNEL_TAG = "channel";
    static final String NAME_TAG = "name";
    static final String TYPE_TAG = "type";
    static final String SIZE_TAG = "size";
    static final String UPDATE_RATE_TAG = "updateRate";
    /**
     * Retrieves a set with all the EPICS channels
     * that can be simulated in the GMP.
     * @return Set of all the EPICS channels that can be
     * simulated in the GMP
     */

    Set<SimulatedEpicsChannel> getSimulatedChannels();
}
