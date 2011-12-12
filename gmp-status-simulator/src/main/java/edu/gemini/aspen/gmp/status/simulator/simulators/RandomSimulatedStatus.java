package edu.gemini.aspen.gmp.status.simulator.simulators;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.gmp.status.simulator.SimulatedStatus;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

/**
 * Simulates a status channel using random values
 */
public abstract class RandomSimulatedStatus<T> implements SimulatedStatus<T> {
    private final String name;
    protected final Random rnd = new SecureRandom();

    public RandomSimulatedStatus(String name) {
        this.name = name;
    }

    @Override
    public StatusItem<T> simulateOnce() {
        return new BasicStatus<T>(name, generateValue(), new Date());
    }

    abstract T generateValue();
}
