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
public abstract class BaseStatusSimulator<T> implements SimulatedStatus<T> {
    private final String name;
    private final long updateRate;
    protected final Random rnd = new SecureRandom();

    public BaseStatusSimulator(String name, long updateRate) {
        this.name = name;
        this.updateRate = updateRate;
    }

    @Override
    public StatusItem<T> simulateOnce() {
        return new BasicStatus<T>(name, generateValue(), new Date());
    }

    @Override
    public long getUpdateRate() {
        return updateRate;
    }

    @Override
    public String getName() {
        return name;
    }

    abstract T generateValue();

}
