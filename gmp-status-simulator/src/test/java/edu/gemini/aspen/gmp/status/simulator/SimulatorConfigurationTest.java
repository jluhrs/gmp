package edu.gemini.aspen.gmp.status.simulator;

import org.junit.Test;

import javax.xml.bind.JAXBException;

public class SimulatorConfigurationTest {
    @Test
    public void testSimpleConfiguration() throws JAXBException {
        System.out.println(getClass().getResourceAsStream("status-simulator.xml"));
        SimulatorConfiguration configuration = new SimulatorConfiguration(getClass().getResourceAsStream("status-simulator.xml"));
        /*Set<? extends SimulatedEpicsChannel> simulatedChannels = configuration.getSimulatedChannels();
        assertNotNull(simulatedChannels);
        assertEquals(2, simulatedChannels.size());

        assertTrue(simulatedChannels.contains(SimulatedEpicsChannel.buildSimulatedEpicsChannel("ws:wsFilter.VALL", 10, DataType.DOUBLE, 1000L)));
        assertTrue(simulatedChannels.contains(SimulatedEpicsChannel.buildSimulatedEpicsChannel("ws:cpWf", 20, DataType.BYTE, 7000L)));*/
    }
}