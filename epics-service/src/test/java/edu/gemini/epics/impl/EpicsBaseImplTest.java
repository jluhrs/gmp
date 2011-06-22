package edu.gemini.epics.impl;

import com.cosylab.epics.caj.CAJChannel;
import com.cosylab.epics.caj.CAJContext;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsService;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EpicsBaseImplTest {
    private static final String CHANNEL_NAME = "tst:tst";
    private final CAJContext context = mock(CAJContext.class);
    private final CAJChannel channel = mock(CAJChannel.class);

    @Test
    public void testChannelBinding() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        EpicsBaseImpl epicsBase = new EpicsBaseImpl(new EpicsService(context));
        epicsBase.bindChannel(CHANNEL_NAME);

        assertNotNull(epicsBase.getChannel(CHANNEL_NAME));
    }

    @Test
    public void testChannelUnbinding() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        EpicsBaseImpl epicsBase = new EpicsBaseImpl(new EpicsService(context));
        epicsBase.bindChannel(CHANNEL_NAME);
        epicsBase.unbindChannel(CHANNEL_NAME);

        assertNull(epicsBase.getChannel(CHANNEL_NAME));
    }

    @Test
    public void testChannelUnbindingUnknowChannel() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        EpicsBaseImpl epicsBase = new EpicsBaseImpl(new EpicsService(context));
        epicsBase.unbindChannel(CHANNEL_NAME);

        assertNull(epicsBase.getChannel(CHANNEL_NAME));
    }

    @Test
    public void testFindUnknownChannel() {
        EpicsBaseImpl epicsBase = new EpicsBaseImpl(new EpicsService(context));

        assertNull(epicsBase.getChannel(CHANNEL_NAME));
    }

    @Test
    public void testIsChannelKnown() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);
        EpicsBaseImpl epicsBase = new EpicsBaseImpl(new EpicsService(context));

        assertFalse(epicsBase.isChannelKnown(CHANNEL_NAME));

        epicsBase.bindChannel(CHANNEL_NAME);

        assertTrue(epicsBase.isChannelKnown(CHANNEL_NAME));
    }

    @Test
    public void testClose() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        // Bind a channel
        EpicsBaseImpl epicsBase = new EpicsBaseImpl(new EpicsService(context));
        epicsBase.bindChannel(CHANNEL_NAME);

        // close
        epicsBase.close();
        assertNull(epicsBase.getChannel(CHANNEL_NAME));
    }

    @Test(expected = EpicsException.class)
    public void testCAExceptionWhileBindingChannel() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenThrow(new CAException());

        EpicsBaseImpl epicsBase = new EpicsBaseImpl(new EpicsService(context));
        epicsBase.bindChannel(CHANNEL_NAME);
    }
    
    @Test(expected = EpicsException.class)
    public void testIllegalStateExceptionWhileBindingChannel() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenThrow(new IllegalStateException());

        EpicsBaseImpl epicsBase = new EpicsBaseImpl(new EpicsService(context));
        epicsBase.bindChannel(CHANNEL_NAME);
    }

    @Test(expected = EpicsException.class)
    public void testTimeoutWhileBindingChannel() throws CAException, TimeoutException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);
        doThrow(new TimeoutException()).when(context).pendIO(anyDouble());

        EpicsBaseImpl epicsBase = new EpicsBaseImpl(new EpicsService(context));
        epicsBase.bindChannel(CHANNEL_NAME);
    }
}
