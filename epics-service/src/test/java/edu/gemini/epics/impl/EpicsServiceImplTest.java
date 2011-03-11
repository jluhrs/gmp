package edu.gemini.epics.impl;

import com.google.common.collect.ImmutableMap;
import edu.gemini.epics.IEpicsClient;
import gov.aps.jca.Context;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class EpicsServiceImplTest {
    @Test
    public void testNormalProcessing() {
        EpicsServiceImpl epicsService = new EpicsServiceImpl();
        epicsService.startService();

        assertNotNull(epicsService.getJCAContext());

        epicsService.stopService();
    }

    @Test
    public void testBindingEpicsClient() {
        EpicsServiceImpl epicsService = new EpicsServiceImpl();
        epicsService.startService();

        IEpicsClient epicsClient = mock(IEpicsClient.class);
        epicsService.bindEpicsClient(epicsClient, ImmutableMap.<String, Object>of(IEpicsClient.EPICS_CHANNELS, new String[] {"tst:tst"}));

        epicsService.stopService();

        verify(epicsClient).connected();
    }

    @Test
    public void testBindingEpicsClientBeforeStartingTheService() {
        EpicsServiceImpl epicsService = new EpicsServiceImpl();

        IEpicsClient epicsClient = mock(IEpicsClient.class);
        epicsService.bindEpicsClient(epicsClient, ImmutableMap.<String, Object>of(IEpicsClient.EPICS_CHANNELS, new String[]{"tst:tst"}));
        verifyZeroInteractions(epicsClient);
        epicsService.startService();

        verify(epicsClient).connected();
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
