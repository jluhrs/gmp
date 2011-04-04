package edu.gemini.epics;

import com.google.common.collect.ImmutableMap;
import gov.aps.jca.Context;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

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

    @Test(expected = IllegalStateException.class)
    public void testGetContextBeforeReady() {
        epicsService.getJCAContext();
    }

    @Test
    public void testGetContextPassedInitially() {
        Context context = mock(Context.class);
        EpicsService epicsService = new EpicsService(context);
        assertEquals(context, epicsService.getJCAContext());
    }
}
