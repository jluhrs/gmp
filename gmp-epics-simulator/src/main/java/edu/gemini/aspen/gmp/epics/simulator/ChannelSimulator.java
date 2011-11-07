package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.simulator.channels.SimulatedEpicsChannel;

import java.util.logging.Logger;

/**
 * A Channel Simulator provides a Runnable task that will take care of
 * updating the content of a simulated EPICS channels. The simulation
 * is executed based on the parameters of the simulated channel; they
 * define the data type to update, the number of elements to update and
 * the update rate
 * <br>
 * Channel Simulator tasks are started by the Simulator, via the
 * {@link Simulator#startSimulation(edu.gemini.aspen.gmp.epics.simulator.channels.SimulatedEpicsChannel)} method.
 */
public class ChannelSimulator implements Runnable {

    private static final Logger LOG = Logger.getLogger(ChannelSimulator.class.getName());

    private EpicsRegistrar _registrar;

    private final SimulatedEpicsChannel _simulatedChannel;

    /**
     * Creates a new Channel simulator tasks, indicating the channel to simulate
     * and the EPICS registrar to be notified of changes
     *
     * @param channel   the channel to simulate
     * @param registrar EpicsRegistrar to notify when updates occur
     */
    public ChannelSimulator(SimulatedEpicsChannel channel, EpicsRegistrar registrar) {
        _simulatedChannel = channel;
        _registrar = registrar;
    }

    /**
     * Execution thread of this simulated channel.
     */
    public void run() {
        EpicsUpdate<?> newUpdate = _simulatedChannel.buildEpicsUpdate();
        LOG.fine("New simulated value for " + _simulatedChannel.getName() + " " + newUpdate);
        _registrar.processEpicsUpdate(newUpdate);
    }
}
