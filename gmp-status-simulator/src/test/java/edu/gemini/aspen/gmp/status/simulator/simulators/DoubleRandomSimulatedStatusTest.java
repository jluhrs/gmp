package edu.gemini.aspen.gmp.status.simulator.simulators;

import edu.gemini.aspen.giapi.status.StatusItem;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DoubleRandomSimulatedStatusTest {
    private String name;

    @Before
    public void setUp() throws Exception {
        name = "tst:status1";
    }

    @Test
    public void testCreation() {
        BaseStatusSimulator<Double> statusSimulator = new DoubleRandomStatusSimulator(name, 100, 0.0, 1.0);
        assertNotNull(statusSimulator);
        assertEquals(100, statusSimulator.getUpdateRate());
    }

    @Test
    public void testSimulateOnceDouble() {
        BaseStatusSimulator<Double> statusSimulator = new DoubleRandomStatusSimulator(name, 100, 0.0, 100);
        for (int i = 0; i < 10000; i++) {
            StatusItem<Double> statusItem = statusSimulator.simulateOnce();
            assertNotNull(statusItem);
            assertEquals(name, statusItem.getName());
            assertTrue(statusItem.getValue() >= 0);
            assertTrue(statusItem.getValue() <= 100);
            assertTrue((statusItem.getTimestamp().getTime() - new Date().getTime()) < 1000);
        }
    }
}