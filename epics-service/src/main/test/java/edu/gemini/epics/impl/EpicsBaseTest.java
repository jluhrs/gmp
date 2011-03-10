package edu.gemini.epics.impl;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EpicsBaseTest {
    @Test
    public void testChannelBinding() throws CAException {
        Context context = mock(Context.class);
        String channelName = "tst:tst";
        Channel channel = mock(Channel.class);
        when(context.createChannel(channelName)).thenReturn(channel);

        EpicsBase epicsBase = new EpicsBase(context);
        epicsBase.bindChannel(channelName);

        assertNotNull(epicsBase.getChannel(channelName));
    }

    @Test
    public void testFindUnknownChannel() {
        Context context = mock(Context.class);
        String channelName = "tst:tst";

        EpicsBase epicsBase = new EpicsBase(context);

        assertNull(epicsBase.getChannel(channelName));
    }
}
