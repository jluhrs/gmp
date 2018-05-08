package edu.gemini.epics.impl;

import com.cosylab.epics.caj.CAJContext;
import com.google.common.collect.ImmutableList;
import edu.gemini.epics.EpicsObserver;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.api.EpicsClient;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.JCALibrary;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration test that verifies that we get updates from the Weather Station
 */
@Ignore
public class ConnectionCleanupIT {

    private ChannelBindingSupport cbs;
    private CAJContext context;

    @Before
    public void setUp() throws Exception {
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "172.16.2.20");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");

        context = (CAJContext) JCALibrary.getInstance().createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
    }

    @After
    public void cleanUp() throws CAException {
        context.destroy();
    }

    @Test
    public void testChannelCleanup() throws InterruptedException {
        TimeUpdatesClient timeUpdatesClient = new TimeUpdatesClient();

        EpicsService epicsService = new EpicsService(context);
        EpicsObserver observer = new EpicsObserverImpl(epicsService);

        observer.registerEpicsClient(timeUpdatesClient, ImmutableList.of("tc1:LST"));
        // Give it 1 second to find the channel
        TimeUnit.MILLISECONDS.sleep(1000);
        assertTrue(context.getChannels().length == 1);
        assertEquals("tc1:LST", context.getChannels()[0].getName());
        for (Channel c:context.getChannels()) {
            System.out.println(c.getName());
        }

        // Give it 5 seconds
        TimeUnit.MILLISECONDS.sleep(5000);

        // Unregister client
        observer.unregisterEpicsClient(timeUpdatesClient);

        TimeUnit.MILLISECONDS.sleep(1000);
        assertEquals(0, context.getChannels().length);

    }

    private class TimeUpdatesClient implements EpicsClient {
        public <T> void valueChanged(String channel, List<T> values) {
            System.out.println(values);
        }

        public void connected() {
        }

        public void disconnected() {
        }

    }
}
