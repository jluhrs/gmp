package edu.gemini.aspen.gmp.status.simulator;

import edu.gemini.aspen.giapi.status.StatusItem;

/**
 * Represents a simulated status channel
 */
public interface SimulatedStatus<T> {
    StatusItem<T> simulateOnce();
}
