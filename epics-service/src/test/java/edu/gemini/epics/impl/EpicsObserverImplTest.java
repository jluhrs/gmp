package edu.gemini.epics.impl;

import com.google.common.collect.ImmutableList;
import edu.gemini.epics.EpicsClientMock;
import edu.gemini.epics.EpicsObserver;
import edu.gemini.epics.JCAContextController;
import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.event.ConnectionListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class EpicsObserverImplTest {
    private static final ImmutableList<String> CHANNELS_TO_READ = ImmutableList.of("tst:tst");
    private EpicsObserver epicsObserver;
    private JCAContextController contextController;
    private Context jcaContext;

    @Before
    public void setUp() throws Exception {
        contextController = mock(JCAContextController.class);
        when(contextController.isContextAvailable()).thenReturn(true);

        jcaContext = mock(Context.class);
        when(contextController.getJCAContext()).thenReturn(jcaContext);

        epicsObserver = new EpicsObserverImpl(contextController);
    }

    @Test
    public void testBindingEpicsClient() throws CAException {
        EpicsClientMock epicsClient = new EpicsClientMock();
        epicsObserver.registerEpicsClient(epicsClient, CHANNELS_TO_READ);

        assertTrue(epicsClient.wasConnectedCalled());
        verify(jcaContext).createChannel(eq("tst:tst"), Matchers.<ConnectionListener>anyObject());
    }

    @Test
    public void testBindingEpicsClientWithNoChannelsProperty() {
        EpicsClientMock epicsClient = new EpicsClientMock();
        epicsObserver.registerEpicsClient(epicsClient, ImmutableList.<String>of());

        assertFalse(epicsClient.wasConnectedCalled());
        verifyZeroInteractions(jcaContext);
    }
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
