package edu.gemini.aspen.gmp.status.simulator.simulators;

import edu.gemini.aspen.gmp.status.simulator.SimulatedStatus;
import edu.gemini.aspen.gmp.status.simulator.generated.StatusType;

/**
 * Factory class for simulated status objects
 */
public interface StatusSimulatorFactory {
    SimulatedStatus buildStatusSimulator(StatusType s);
}
