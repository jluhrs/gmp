package edu.gemini.aspen.gmp.status.simulator.simulators;

import edu.gemini.aspen.giapi.status.StatusItem;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AsymptoticWithNoiseStatusSimulatorTest {
    private String name;

    @Before
    public void setUp() throws Exception {
        name = "tst:status1";
    }

    @Test
    public void testCreation() {
        BaseStatusSimulator<Double> statusSimulator = new AsymptoticWithNoiseStatusSimulatorSimulator(name, 100, 0, 1.0, 1000, 0.1);
        assertNotNull(statusSimulator);
        assertEquals(100, statusSimulator.getUpdateRate());
        assertEquals(name, statusSimulator.getName());
    }

    @Test
    public void testSimulateOnceDouble() throws InterruptedException {
        BaseStatusSimulator<Double> statusSimulator = new AsymptoticWithNoiseStatusSimulatorSimulator(name, 100, 0, 1.0, 1000, 0.1);
        for (int i = 0; i < 1000; i++) {
            StatusItem<Double> statusItem = statusSimulator.simulateOnce();
            assertNotNull(statusItem);
            assertTrue(statusItem.getValue() >= 0);
            assertTrue(statusItem.getValue() <= 1+0.1);
            assertTrue((statusItem.getTimestamp().getTime() - new Date().getTime()) < 1000);
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }
}
