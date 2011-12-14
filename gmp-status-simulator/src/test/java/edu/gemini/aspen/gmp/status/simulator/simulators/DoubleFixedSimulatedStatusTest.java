package edu.gemini.aspen.gmp.status.simulator.simulators;

import edu.gemini.aspen.giapi.status.StatusItem;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DoubleFixedSimulatedStatusTest {
    private String name;

    @Before
    public void setUp() throws Exception {
        name = "tst:status1";
    }

    @Test
    public void testCreation() {
        BaseStatusSimulator<Double> statusSimulator = new DoubleFixedStatusSimulator(name, 100, 1.0);
        assertNotNull(statusSimulator);
        assertEquals(100, statusSimulator.getUpdateRate());
    }

    @Test
    public void testSimulateOnceDouble() {
        BaseStatusSimulator<Double> statusSimulator = new DoubleFixedStatusSimulator(name, 100, 1.0);
        StatusItem<Double> statusItem = statusSimulator.simulateOnce();
        assertNotNull(statusItem);
        assertEquals(name, statusItem.getName());
        assertEquals(1.0, statusItem.getValue(), 0);
        assertTrue((statusItem.getTimestamp().getTime() - new Date().getTime()) < 1000);
    }
}
