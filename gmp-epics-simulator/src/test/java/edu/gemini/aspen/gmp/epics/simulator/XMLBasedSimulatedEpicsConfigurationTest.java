package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.simulator.channels.SimulatedEpicsChannel;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class XMLBasedSimulatedEpicsConfigurationTest {
    @Test
    public void testSimpleConfiguration() {
        XMLBasedSimulatedEpicsConfiguration configuration = new XMLBasedSimulatedEpicsConfiguration(getClass().getResourceAsStream("simulated-epics-channels.xml"));
        Set<? extends SimulatedEpicsChannel> simulatedChannels = configuration.getSimulatedChannels();
        assertNotNull(simulatedChannels);
        assertEquals(2, simulatedChannels.size());

        assertTrue(simulatedChannels.contains(SimulatedEpicsChannel.buildSimulatedEpicsChannel("ws:wsFilter.VALL", 10, DataType.DOUBLE, 1000L)));
        assertTrue(simulatedChannels.contains(SimulatedEpicsChannel.buildSimulatedEpicsChannel("ws:cpWf", 20, DataType.BYTE, 7000L)));
    }
}
