package edu.gemini.epics;

import edu.gemini.epics.impl.EpicsServiceImpl;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class EpicsServiceTest {
    @Test
    public void testValidate() {
        EpicsServiceImpl epicsService = new EpicsServiceImpl(null);
        epicsService.startService();

        assertNotNull(epicsService.getJCAContext());

        epicsService.stopService();
    }
}
