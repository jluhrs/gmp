package edu.gemini.aspen.gmp.status.simulator.simulators;

import edu.gemini.aspen.giapi.status.StatusItem;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AsymptoticWithNoiseSimulatedStatusTest {
    private String name;

    @Before
    public void setUp() throws Exception {
        name = "tst:status1";
    }

    @Test
    public void testCreation() {
        BaseSimulatedStatus<Double> status = new AsymptoticWithNoiseSimulatedStatus(name, 100, 0, 1.0, 1000, 0.1);
        assertNotNull(status);
        assertEquals(100, status.getUpdateRate());
        assertEquals(name, status.getName());
    }

    @Test
    public void testSimulateOnceDouble() throws InterruptedException {
        BaseSimulatedStatus<Double> status = new AsymptoticWithNoiseSimulatedStatus(name, 100, 0, 1.0, 1000, 0.1);
        for (int i = 0; i < 1000; i++) {
            StatusItem<Double> statusItem = status.simulateOnce();
            assertNotNull(statusItem);
            assertTrue(statusItem.getValue() >= 0);
            assertTrue(statusItem.getValue() <= 1+0.1);
            assertTrue((statusItem.getTimestamp().getTime() - new Date().getTime()) < 1000);
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }
}
