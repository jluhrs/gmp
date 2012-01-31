package edu.gemini.aspen.gmp.status.simulator.simulators;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StringEnumerationSimulatedStatusTest {
    private String name;

    @Before
    public void setUp() throws Exception {
        name = "tst:status1";
    }

    @Test
    public void testCreation() {
        BaseStatusSimulator<String> statusSimulator = new StringEnumerationStatusSimulator(name, 100, ImmutableList.of("A", "B", "C"));
        assertNotNull(statusSimulator);
        assertEquals(100, statusSimulator.getUpdateRate());
        assertEquals(name, statusSimulator.getName());
    }

    @Test
    public void testSimulateOnce() {
        BaseStatusSimulator<String> statusSimulator = new StringEnumerationStatusSimulator(name, 100, ImmutableList.<String>of("A", "B", "C"));
        assertEquals("A", statusSimulator.simulateOnce().getValue());
        assertEquals("B", statusSimulator.simulateOnce().getValue());
        assertEquals("C", statusSimulator.simulateOnce().getValue());
        // Should cycle the values
        assertEquals("A", statusSimulator.simulateOnce().getValue());
    }
}
