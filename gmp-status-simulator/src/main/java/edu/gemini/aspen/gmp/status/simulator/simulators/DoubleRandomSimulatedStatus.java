package edu.gemini.aspen.gmp.status.simulator.simulators;

/**
 * Simulates random double status items
 */
public class DoubleRandomSimulatedStatus extends RandomSimulatedStatus<Double> {
    public DoubleRandomSimulatedStatus(String name, long updateRate) {
        super(name, updateRate);
    }

    @Override
    Double generateValue() {
        return rnd.nextDouble();
    }
}
