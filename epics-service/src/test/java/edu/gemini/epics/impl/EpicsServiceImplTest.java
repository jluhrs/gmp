package edu.gemini.epics.impl;

import gov.aps.jca.Context;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class EpicsServiceImplTest {
    @Test
    public void testNormalProcessing() {
        EpicsServiceImpl epicsService = new EpicsServiceImpl();
        epicsService.startService();

        assertNotNull(epicsService.getJCAContext());

        epicsService.stopService();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContextBeforeReady() {
        EpicsServiceImpl epicsService = new EpicsServiceImpl();

        epicsService.getJCAContext();
    }

    @Test
    public void testGetContextPassedInitially() {
        Context context = mock(Context.class);
        EpicsServiceImpl epicsService = new EpicsServiceImpl(context);
        assertEquals(context, epicsService.getJCAContext());
    }
}
