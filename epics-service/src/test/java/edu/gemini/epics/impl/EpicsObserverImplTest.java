package edu.gemini.epics.impl;

import com.google.common.collect.ImmutableList;
import edu.gemini.epics.EpicsClient;
import edu.gemini.epics.EpicsClientMock;
import edu.gemini.epics.JCAContextController;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
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
    private EpicsObserverImpl epicsObserver;
    private JCAContextController contextController;
    private Context jcaContext;

    @Before
    public void setUp() throws Exception {
        contextController = mock(JCAContextController.class);
        when(contextController.isContextAvailable()).thenReturn(true);

        jcaContext = mock(Context.class);
        when(contextController.getJCAContext()).thenReturn(jcaContext);
        Channel channel = mock(Channel.class);
        when(jcaContext.createChannel(Matchers.<String>any(), Matchers.<ConnectionListener>anyObject())).thenReturn(channel);

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

    @Test
    public void testBindingEpicsClientBeforeStartingTheService() throws CAException {
        when(contextController.isContextAvailable()).thenReturn(false);

        EpicsClientMock epicsClient =  new EpicsClientMock();
        epicsObserver.registerEpicsClient(epicsClient, CHANNELS_TO_READ);

        verifyZeroInteractions(jcaContext);

        when(contextController.isContextAvailable()).thenReturn(true);
        when(contextController.getJCAContext()).thenReturn(jcaContext);
        
        epicsObserver.startObserver();

        verify(jcaContext).createChannel(eq("tst:tst"), Matchers.<ConnectionListener>anyObject());
        assertTrue(epicsClient.wasConnectedCalled());
    }

    @Test
    public void testUnregisterEpicsClient() {
        EpicsClientMock epicsClient = new EpicsClientMock();
        epicsObserver.registerEpicsClient(epicsClient, CHANNELS_TO_READ);

        epicsObserver.unregisterEpicsClient(epicsClient);
        assertTrue(epicsClient.wasDisconnectedCalled());
    }

    @Test
    public void testUnbindingEpicsClientWhenStopping() {
        EpicsClientMock epicsClient = new EpicsClientMock();
        epicsObserver.registerEpicsClient(epicsClient, CHANNELS_TO_READ);

        epicsObserver.stopObserver();

        assertTrue(epicsClient.wasDisconnectedCalled());
    }

    @Test
    public void testUnbindingUnknownClient() {
        EpicsClient epicsClient = mock(EpicsClient.class);
        epicsObserver.unregisterEpicsClient(epicsClient);

        verifyZeroInteractions(epicsClient);
    }

}
