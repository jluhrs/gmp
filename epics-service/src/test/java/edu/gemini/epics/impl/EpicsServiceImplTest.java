package edu.gemini.epics.impl;

import com.google.common.collect.ImmutableMap;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.IEpicsClient;
import gov.aps.jca.Context;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class EpicsServiceImplTest {
    private static final ImmutableMap<String,Object> CHANNELS_TO_READ = ImmutableMap.<String, Object>of(IEpicsClient.EPICS_CHANNELS, new String[] {"tst:tst"});
    private EpicsServiceImpl epicsService;

    @Before
    public void setUp() throws Exception {
        epicsService = new EpicsServiceImpl();
    }

    @Test
    public void testNormalProcessing() {
        epicsService.startService();

        assertNotNull(epicsService.getJCAContext());

        epicsService.stopService();
    }

    @Test
    public void testBindingEpicsClient() {
        epicsService.startService();

        IEpicsClient epicsClient = mock(IEpicsClient.class);
        epicsService.bindEpicsClient(epicsClient, CHANNELS_TO_READ);

        epicsService.stopService();

        verify(epicsClient).connected();
    }

    @Test
    public void testBindingEpicsClientBeforeStartingTheService() {
        IEpicsClient epicsClient = mock(IEpicsClient.class);
        epicsService.bindEpicsClient(epicsClient, CHANNELS_TO_READ);
        
        verifyZeroInteractions(epicsClient);
        epicsService.startService();

        verify(epicsClient).connected();
        epicsService.stopService();
    }

    @Test
    public void testUnbindingEpicsClient() {
        epicsService.startService();
        IEpicsClient epicsClient = mock(IEpicsClient.class);
        epicsService.bindEpicsClient(epicsClient, CHANNELS_TO_READ);
        verify(epicsClient).connected();

        epicsService.unbindEpicsClient(epicsClient);
        verify(epicsClient).disconnected();

        epicsService.stopService();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContextBeforeReady() {
        epicsService.getJCAContext();
    }

    @Test
    public void testGetContextPassedInitially() {
        Context context = mock(Context.class);
        EpicsService epicsService = new EpicsServiceImpl(context);
        assertEquals(context, epicsService.getJCAContext());
    }
}
