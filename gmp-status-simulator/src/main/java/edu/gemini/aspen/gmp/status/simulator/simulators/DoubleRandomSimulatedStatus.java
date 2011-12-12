package edu.gemini.aspen.gmp.status.simulator.simulators;

/**
 * Simulates random double status items
 */
class DoubleRandomSimulatedStatus extends RandomSimulatedStatus<Double> {
    public DoubleRandomSimulatedStatus(String name) {
        super(name);
    }

    @Override
    Double generateValue() {
        return rnd.nextDouble();
    }
}
