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
        BaseSimulatedStatus<Double> status = new DoubleFixedSimulatedStatus(name, 100, 1.0);
        assertNotNull(status);
        assertEquals(100, status.getUpdateRate());
    }

    @Test
    public void testSimulateOnceDouble() {
        BaseSimulatedStatus<Double> status = new DoubleFixedSimulatedStatus(name, 100, 1.0);
        StatusItem<Double> statusItem = status.simulateOnce();
        assertNotNull(statusItem);
        assertEquals(name, statusItem.getName());
        assertEquals(1.0, statusItem.getValue(), 0);
        assertTrue((statusItem.getTimestamp().getTime() - new Date().getTime()) < 1000);
    }
}
