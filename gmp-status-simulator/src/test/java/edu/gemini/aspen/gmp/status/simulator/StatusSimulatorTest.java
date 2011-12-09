package edu.gemini.aspen.gmp.status.simulator;

import org.junit.Test;

import javax.xml.bind.JAXBException;

import static org.junit.Assert.assertNotNull;

public class StatusSimulatorTest {
    private StatusSimulator component;

    @Test
    public void testCreation() throws InterruptedException, JAXBException {
        component = new StatusSimulator(new SimulatorConfiguration(getClass().getResourceAsStream("status-simulator.xml")));
        assertNotNull(component);
//        component = new EpicsSimulatorComponent(registrar, configFile.getAbsolutePath());
//        component.readConfiguration();
//        component.startSimulation();
//
//        // Wait for one update
//        TimeUnit.MILLISECONDS.sleep(1100L);
//
//        verify(registrar, atLeastOnce()).processEpicsUpdate(Matchers.<EpicsUpdate<?>>anyObject());
    }
}
