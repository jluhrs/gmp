package edu.gemini.aspen.gmp.status.simulator.simulators;

/**
 * Creates a status with a fixed double value
 */
public class DoubleFixedSimulatedStatus extends RandomSimulatedStatus<Double> {
    private final Double value;

    public DoubleFixedSimulatedStatus(String name, long updateRate, Double value) {
        super(name, updateRate);
        this.value = value;
    }
    @Override
    Double generateValue() {
        return value;
    }
}
