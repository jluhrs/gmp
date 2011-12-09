package edu.gemini.aspen.gmp.status.simulator;

import edu.gemini.aspen.gmp.status.simulator.generated.StatusType;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SimulatorConfigurationTest {
    @Test
    public void testSimpleConfiguration() throws JAXBException {
        SimulatorConfiguration configuration = new SimulatorConfiguration(getClass().getResourceAsStream("status-simulator.xml"));
        List<StatusType> statuses = configuration.getStatuses();
        assertEquals(2, statuses.size());
    }
}