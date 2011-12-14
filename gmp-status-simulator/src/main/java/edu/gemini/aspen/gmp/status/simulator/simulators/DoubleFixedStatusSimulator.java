package edu.gemini.aspen.gmp.status.simulator.simulators;

/**
 * Creates a status with a fixed double value
 */
public class DoubleFixedStatusSimulator extends BaseStatusSimulator<Double> {
    private final Double value;

    public DoubleFixedStatusSimulator(String name, long updateRate, Double value) {
        super(name, updateRate);
        this.value = value;
    }
    @Override
    Double generateValue() {
        return value;
    }
}
