package edu.gemini.epics.impl;

import edu.gemini.epics.IEpicsClient;
import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.event.ConnectionListener;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ChannelBindingSupportTest {
    private static final String CHANNEL_NAME = "tst:tst";

    @Test
    public void testBindingChannel() throws CAException {
        Context context = mock(Context.class);
        IEpicsClient target = null;
        ChannelBindingSupport cbs = new ChannelBindingSupport(context, target);

        cbs.bindChannel(CHANNEL_NAME);

        verify(context).createChannel(eq(CHANNEL_NAME), any(ConnectionListener.class));
    }
}
