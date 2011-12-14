package edu.gemini.aspen.gmp.status.simulator;

import edu.gemini.aspen.giapi.status.StatusItem;

/**
 * Represents a an object that can simulate a status value
 */
public interface SimulatedStatus<T> {
    StatusItem<T> simulateOnce();

    long getUpdateRate();

    String getName();
}
