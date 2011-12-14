package edu.gemini.aspen.gmp.status.simulator.simulators;

import edu.gemini.aspen.giapi.status.StatusItem;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class IntRandomSimulatedStatusTest {
    private String name;

    @Before
    public void setUp() throws Exception {
        name = "tst:status1";
    }

    @Test
    public void testCreation() {
        BaseStatusSimulator<Integer> statusSimulator = new IntRandomStatusSimulator(name, 100, 0, 10);
        assertNotNull(statusSimulator);
        assertEquals(100, statusSimulator.getUpdateRate());
        assertEquals(name, statusSimulator.getName());
    }

    @Test
    public void testSimulateOnceDouble() {
        BaseStatusSimulator<Integer> statusSimulator = new IntRandomStatusSimulator(name, 100, -30, 30);
        for (int i = 0; i < 10000; i++) {
            StatusItem<Integer> statusItem = statusSimulator.simulateOnce();
            assertNotNull(statusItem);
            assertTrue(statusItem.getValue().intValue() >= -30);
            assertTrue(statusItem.getValue().intValue() <= 30);
            assertTrue((statusItem.getTimestamp().getTime() - new Date().getTime()) < 1000);
        }
    }
}
