package edu.gemini.epics.impl;

import com.google.common.collect.ImmutableList;
import edu.gemini.epics.EpicsClient;
import edu.gemini.epics.EpicsObserver;
import edu.gemini.epics.JCAContextController;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class EpicsObserverImplTest {
    private static final ImmutableList<String> CHANNELS_TO_READ = ImmutableList.of("tst:tst");
    private EpicsObserver epicsObserver;
    private JCAContextController contextController;

    @Before
    public void setUp() throws Exception {
        contextController = mock(JCAContextController.class);
        epicsObserver = new EpicsObserverImpl(contextController);
    }

    @Test
    public void testBindingEpicsClient() {
        EpicsClient epicsClient = mock(EpicsClient.class);
        epicsObserver.registerEpicsClient(epicsClient, CHANNELS_TO_READ);

        //verify(epicsClient).connected();
    }

//    @Test
//    public void testBindingEpicsClientWithNoChannelsProperty() {
//        epicsObserver.startService();
//
//        EpicsClient epicsClient = mock(EpicsClient.class);
//        epicsObserver.bindEpicsClient(epicsClient, ImmutableMap.<String, Object>of());
//
//        epicsObserver.stopService();
//
//        verifyZeroInteractions(epicsClient);
//    }
//
//    @Test
//    public void testBindingEpicsClientBeforeStartingTheService() {
//        EpicsClient epicsClient = mock(EpicsClient.class);
//        epicsObserver.bindEpicsClient(epicsClient, CHANNELS_TO_READ);
//
//        verifyZeroInteractions(epicsClient);
//        epicsObserver.startService();
//
//        verify(epicsClient).connected();
//        epicsObserver.stopService();
//    }
//
//    @Test
//    public void testUnbindingEpicsClient() {
//        epicsObserver.startService();
//        EpicsClient epicsClient = mock(EpicsClient.class);
//        epicsObserver.bindEpicsClient(epicsClient, CHANNELS_TO_READ);
//        verify(epicsClient).connected();
//
//        epicsObserver.unbindEpicsClient(epicsClient);
//        verify(epicsClient).disconnected();
//
//        epicsObserver.stopService();
//    }
//
//    @Test
//    public void testUnbindingEpicsClientWhenStopping() {
//        epicsObserver.startService();
//        EpicsClient epicsClient = mock(EpicsClient.class);
//        epicsObserver.bindEpicsClient(epicsClient, CHANNELS_TO_READ);
//        verify(epicsClient).connected();
//
//        epicsObserver.stopService();
//        verify(epicsClient).disconnected();
//    }
//
//    @Test
//    public void testUnbindingUnknownClient() {
//        epicsObserver.startService();
//        EpicsClient epicsClient = mock(EpicsClient.class);
//        epicsObserver.unbindEpicsClient(epicsClient);
//
//        epicsObserver.stopService();
//        verifyZeroInteractions(epicsClient);
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void testGetContextBeforeReady() {
//        epicsObserver.getJCAContext();
//    }
//
//    @Test
//    public void testGetContextPassedInitially() {
//        Context context = mock(Context.class);
//        EpicsService epicsService = new EpicsService(context, "127.0.0.1");
//        assertEquals(context, epicsService.getJCAContext());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testPassingBadAddress() {
//        Context context = mock(Context.class);
//        new EpicsService(context, "a.b.c.d");
//    }
}
