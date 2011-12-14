package edu.gemini.aspen.gmp.status.simulator.simulators;

/**
 * Simulates random double status items
 */
public class IntRandomSimulatedStatus extends BaseSimulatedStatus<Integer> {
    private final int minValue;
    private final int maxValue;

    public IntRandomSimulatedStatus(String name, long updateRate, int minValue, int maxValue) {
        super(name, updateRate);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    Integer generateValue() {
        return rnd.nextInt(maxValue - minValue + 1) + minValue;
    }
}
