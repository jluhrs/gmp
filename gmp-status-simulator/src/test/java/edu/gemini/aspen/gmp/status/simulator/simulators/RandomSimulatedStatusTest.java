package edu.gemini.aspen.gmp.status.simulator.simulators;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class RandomSimulatedStatusTest {
    @Test
    public void testCreation() {
        RandomSimulatedStatus status = new RandomSimulatedStatus();
        assertNotNull(status);
    }
}
