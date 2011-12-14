package edu.gemini.aspen.gmp.status.simulator.simulators;

import edu.gemini.aspen.giapi.status.StatusItem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NullSimulatedStatusTest {
    private String name;

    @Before
    public void setUp() throws Exception {
        name = "tst:status1";
    }

    @Test
    public void testCreation() {
        NullSimulatedStatus status = new NullSimulatedStatus(name);
        assertNotNull(status);
        assertEquals(1000, status.getUpdateRate());
        assertEquals(name, status.getName());
    }

    @Test
    public void testSimulateOnce() {
        NullSimulatedStatus status = new NullSimulatedStatus(name);
        StatusItem<Integer> statusItem = status.simulateOnce();
        assertEquals(Integer.valueOf(0), statusItem.getValue());
    }
}