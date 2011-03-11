package edu.gemini.epics.impl;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class EpicsServiceImplTest {
    @Test
    public void testNormalProcessing() {
        EpicsServiceImpl epicsService = new EpicsServiceImpl();
        epicsService.startService();

        assertNotNull(epicsService.getJCAContext());

        epicsService.stopService();
    }
}
