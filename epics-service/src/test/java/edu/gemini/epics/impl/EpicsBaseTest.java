package edu.gemini.epics.impl;

import edu.gemini.epics.EpicsException;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
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

public class EpicsBaseTest {
    private static final String CHANNEL_NAME = "tst:tst";
    private final Context context = mock(Context.class);
    private final Channel channel = mock(Channel.class);

    @Test
    public void testChannelBinding() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        EpicsBase epicsBase = new EpicsBase(new EpicsServiceImpl(context));
        epicsBase.bindChannel(CHANNEL_NAME);

        assertNotNull(epicsBase.getChannel(CHANNEL_NAME));
    }

    @Test
    public void testFindUnknownChannel() {
        EpicsBase epicsBase = new EpicsBase(new EpicsServiceImpl(context));

        assertNull(epicsBase.getChannel(CHANNEL_NAME));
    }

    @Test
    public void testIsChannelKnown() {
        EpicsBase epicsBase = new EpicsBase(new EpicsServiceImpl(context));

        assertFalse(epicsBase.isChannelKnown(CHANNEL_NAME));

        epicsBase.bindChannel(CHANNEL_NAME);

        assertTrue(epicsBase.isChannelKnown(CHANNEL_NAME));
    }

    @Test
    public void testClose() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        // Bind a channel
        EpicsBase epicsBase = new EpicsBase(new EpicsServiceImpl(context));
        epicsBase.bindChannel(CHANNEL_NAME);

        // close
        epicsBase.close();
        assertNull(epicsBase.getChannel(CHANNEL_NAME));
    }

    @Test(expected = EpicsException.class)
    public void testCAExceptionWhileBindingChannel() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenThrow(new CAException());

        EpicsBase epicsBase = new EpicsBase(new EpicsServiceImpl(context));
        epicsBase.bindChannel(CHANNEL_NAME);
    }

    @Test(expected = EpicsException.class)
    public void testTimeoutWhileBindingChannel() throws CAException, TimeoutException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);
        doThrow(new TimeoutException()).when(context).pendIO(anyDouble());

        EpicsBase epicsBase = new EpicsBase(new EpicsServiceImpl(context));
        epicsBase.bindChannel(CHANNEL_NAME);
    }
}
