package edu.gemini.aspen.gmp.status.simulator.simulators;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.gmp.status.simulator.SimulatedStatus;

/**
 * Null simulated status is used when no status item is possible to configure rather than
 * failing
 */
public class NullSimulatedStatus implements SimulatedStatus<Integer> {
    private final String name;

    NullSimulatedStatus(String name) {
        this.name = name;
    }

    @Override
    public StatusItem<Integer> simulateOnce() {
        // Return always the same value
        return new BasicStatus<Integer>(name, 0);
    }

    @Override
    public long getUpdateRate() {
        return 1000;
    }

    @Override
    public String getName() {
        return name;
    }
}