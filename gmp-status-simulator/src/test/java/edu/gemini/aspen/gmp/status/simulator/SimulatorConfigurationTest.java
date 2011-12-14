package edu.gemini.aspen.gmp.status.simulator;

import edu.gemini.aspen.gmp.status.simulator.generated.StatusType;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimulatorConfigurationTest {
    @Test
    public void testSimpleConfiguration() throws JAXBException {
        SimulatorConfiguration configuration = new SimulatorConfiguration(getClass().getResourceAsStream("status-simulator.xml"));
        List<StatusType> statuses = configuration.getStatuses();
        assertEquals(2, statuses.size());
    }

    @Test
    public void testRandomChannel() throws JAXBException {
        SimulatorConfiguration configuration = new SimulatorConfiguration(getClass().getResourceAsStream("status-simulator.xml"));
        List<StatusType> statuses = configuration.getStatuses();
        boolean verified = false;
        for (StatusType s:statuses) {
            if (s.getName().equals("test:gpi:ao.strehl")) {
                assertEquals("random", s.getMode());
                assertEquals(0, s.getParameters().getStart().intValue());
                verified = true;
            }
        }
        assertTrue("Must have found the tested status", verified);
    }
}