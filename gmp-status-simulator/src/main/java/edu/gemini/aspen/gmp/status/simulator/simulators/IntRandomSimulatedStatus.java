package edu.gemini.aspen.gmp.status.simulator.simulators;

/**
 * Simulates random double status items
 */
public class IntRandomSimulatedStatus extends RandomSimulatedStatus<Integer> {
    public IntRandomSimulatedStatus(String name, long updateRate) {
        super(name, updateRate);
    }

    @Override
    Integer generateValue() {
        return rnd.nextInt();
    }
}
