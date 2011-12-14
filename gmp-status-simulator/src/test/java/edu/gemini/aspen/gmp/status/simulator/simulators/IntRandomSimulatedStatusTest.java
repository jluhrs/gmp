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
        BaseSimulatedStatus<Integer> status = new IntRandomSimulatedStatus(name, 100, 0, 10);
        assertNotNull(status);
        assertEquals(100, status.getUpdateRate());
        assertEquals(name, status.getName());
    }

    @Test
    public void testSimulateOnceDouble() {
        BaseSimulatedStatus<Integer> status = new IntRandomSimulatedStatus(name, 100, -30, 30);
        for (int i = 0; i < 10000; i++) {
            StatusItem<Integer> statusItem = status.simulateOnce();
            assertNotNull(statusItem);
            assertTrue(statusItem.getValue().intValue() >= -30);
            assertTrue(statusItem.getValue().intValue() <= 30);
            assertTrue((statusItem.getTimestamp().getTime() - new Date().getTime()) < 1000);
        }
    }
}
