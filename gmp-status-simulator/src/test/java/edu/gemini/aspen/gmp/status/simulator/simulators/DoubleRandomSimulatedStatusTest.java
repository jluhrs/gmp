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
        RandomSimulatedStatus<Double> status = new DoubleRandomSimulatedStatus(name);
        assertNotNull(status);
    }

    @Test
    public void testSimulateOnceDouble() {
        RandomSimulatedStatus<Double> status = new DoubleRandomSimulatedStatus(name);
        StatusItem<Double> statusItem = status.simulateOnce();
        assertNotNull(statusItem);
        assertEquals(name, statusItem.getName());
        assertEquals(1.0, statusItem.getValue(), 1);
        assertTrue((statusItem.getTimestamp().getTime() - new Date().getTime()) < 1000);
    }
}
