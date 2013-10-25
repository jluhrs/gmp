package edu.gemini.epics;

import com.cosylab.epics.caj.CAJContext;
import com.google.common.collect.ImmutableMap;
import edu.gemini.epics.api.EpicsClient;
import gov.aps.jca.Context;
import org.junit.Before;
import org.junit.Test;

import java.util.Dictionary;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class EpicsServiceTest {
    private static final ImmutableMap<String, Object> CHANNELS_TO_READ = ImmutableMap.<String, Object>of(EpicsClient.EPICS_CHANNELS, new String[]{"tst:tst"});
    private EpicsService epicsService;

    @Before
    public void setUp() throws Exception {
        epicsService = new EpicsService("127.0.0.1");
    }

    @Test
    public void testNormalLifeCycle() {
        epicsService.startService();

        assertNotNull(epicsService.getJCAContext());

        epicsService.stopService();
    }

    @Test
    public void testStartingTwice() {
        epicsService.startService();

        Context context = epicsService.getJCAContext();
        epicsService.startService();

        assertFalse(context.equals(epicsService.getJCAContext()));

        epicsService.stopService();
    }

    @Test
    public void testStopBeforeStart() {
        epicsService.stopService();

        assertFalse(epicsService.isContextAvailable());
    }

    /**
     * Simulates an update from ConfigAdmin
     */
    @Test
    public void updateAddressList() {
        Dictionary<String, String> dictionary = new Hashtable<String, String>();
        String NEW_ADDRESS = "0.0.0.0";
        dictionary.put(EpicsService.PROPERTY_ADDRESS_LIST, NEW_ADDRESS);
        epicsService.changedAddress(dictionary);

        epicsService.startService();
        assertEquals(NEW_ADDRESS, System.getProperty("com.cosylab.epics.caj.CAJContext.addr_list"));

        // Now pass an empty set
        epicsService.changedAddress(new Hashtable<String, String>());
        epicsService.startService();
        assertEquals(NEW_ADDRESS, System.getProperty("com.cosylab.epics.caj.CAJContext.addr_list"));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContextBeforeReady() {
        epicsService.getJCAContext();
    }

    @Test
    public void testGetContextPassedInitially() {
        CAJContext context = mock(CAJContext.class);
        EpicsService epicsService = new EpicsService(context);
        assertEquals(context, epicsService.getJCAContext());
    }

    /**
     * Simulates multiple IP addresses
     */
    @Test
    public void testSupportForMultipleIPs() {
        assertNotNull(new EpicsService("127.0.0.1 0.0.0.0"));
    }

}
