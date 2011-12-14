package edu.gemini.aspen.gmp.status.simulator.simulators;

/**
 * Simulates random double status items
 */
public class DoubleRandomSimulatedStatus extends BaseSimulatedStatus<Double> {
    private final double min;
    private final double max;

    public DoubleRandomSimulatedStatus(String name, long updateRate, double min, double max) {
        super(name, updateRate);
        this.min = min;
        this.max = max;
    }

    @Override
    Double generateValue() {
        return rnd.nextDouble() * (max-min) + min;
    }
}
