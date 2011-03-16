package edu.gemini.epics;

import com.google.common.collect.ImmutableMap;
import gov.aps.jca.Context;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class EpicsServiceTest {
    private static final ImmutableMap<String,Object> CHANNELS_TO_READ = ImmutableMap.<String, Object>of(EpicsClient.EPICS_CHANNELS, new String[] {"tst:tst"});
    private EpicsService epicsService;

    @Before
    public void setUp() throws Exception {
        epicsService = new EpicsService("127.0.0.1");
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

        EpicsClient epicsClient = mock(EpicsClient.class);
        epicsService.bindEpicsClient(epicsClient, CHANNELS_TO_READ);

        epicsService.stopService();

        verify(epicsClient).connected();
    }

    @Test
    public void testBindingEpicsClientBeforeStartingTheService() {
        EpicsClient epicsClient = mock(EpicsClient.class);
        epicsService.bindEpicsClient(epicsClient, CHANNELS_TO_READ);
        
        verifyZeroInteractions(epicsClient);
        epicsService.startService();

        verify(epicsClient).connected();
        epicsService.stopService();
    }

    @Test
    public void testUnbindingEpicsClient() {
        epicsService.startService();
        EpicsClient epicsClient = mock(EpicsClient.class);
        epicsService.bindEpicsClient(epicsClient, CHANNELS_TO_READ);
        verify(epicsClient).connected();

        epicsService.unbindEpicsClient(epicsClient);
        verify(epicsClient).disconnected();

        epicsService.stopService();
    }

    @Test
    public void testUnbindingEpicsClientWhenStopping() {
        epicsService.startService();
        EpicsClient epicsClient = mock(EpicsClient.class);
        epicsService.bindEpicsClient(epicsClient, CHANNELS_TO_READ);
        verify(epicsClient).connected();

        epicsService.stopService();
        verify(epicsClient).disconnected();
    }

    @Test
    public void testUnbindingUnknownClient() {
        epicsService.startService();
        EpicsClient epicsClient = mock(EpicsClient.class);
        epicsService.unbindEpicsClient(epicsClient);

        epicsService.stopService();
        verifyZeroInteractions(epicsClient);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContextBeforeReady() {
        epicsService.getJCAContext();
    }

    @Test
    public void testGetContextPassedInitially() {
        Context context = mock(Context.class);
        EpicsService epicsService = new EpicsService(context, "127.0.0.1");
        assertEquals(context, epicsService.getJCAContext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPassingBadAddress() {
        Context context = mock(Context.class);
        new EpicsService(context, "a.b.c.d");
    }
}
