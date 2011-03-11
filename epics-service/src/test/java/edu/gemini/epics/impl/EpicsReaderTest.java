package edu.gemini.epics.impl;

import edu.gemini.epics.EpicsException;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBR_Float;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EpicsReaderTest {
    private static final String ADDRESS = "172.16.2.24";
    private static final String CHANNEL_NAME = "tst:tst";
    private final Context context = mock(Context.class);
    private final Channel channel = mock(Channel.class);

    @Test
    public void testReadValue() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        float[] simulatedValue = {1, 2};
        when(channel.get()).thenReturn(new DBR_Float(simulatedValue));
        when(channel.getContext()).thenReturn(context);

        EpicsReader epicsReader = new EpicsReader(new EpicsServiceImpl(context, ADDRESS));
        epicsReader.bindChannel(CHANNEL_NAME);

        Object value = epicsReader.getValue(CHANNEL_NAME);
        assertArrayEquals(simulatedValue, (float[]) value, 0.001f);
    }

    @Test
    public void testReadValueOfUnknownChannel() throws CAException {
        EpicsReader epicsReader = new EpicsReader(new EpicsServiceImpl(context, ADDRESS));

        Object value = epicsReader.getValue(CHANNEL_NAME);
        assertNull(value);
    }

    @Test(expected = EpicsException.class)
    public void testReadValueWithCAException() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);
        when(channel.get()).thenThrow(new CAException());

        EpicsReader epicsReader = new EpicsReader(new EpicsServiceImpl(context, ADDRESS));
        epicsReader.bindChannel(CHANNEL_NAME);

        epicsReader.getValue(CHANNEL_NAME);
    }

    @Test(expected = EpicsException.class)
    public void testReadValueWithTimeoutException() throws CAException, TimeoutException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);
        when(channel.getContext()).thenReturn(context);

        EpicsReader epicsReader = new EpicsReader(new EpicsServiceImpl(context, ADDRESS));
        epicsReader.bindChannel(CHANNEL_NAME);

        doThrow(new TimeoutException()).when(context).pendIO(anyDouble());

        epicsReader.getValue(CHANNEL_NAME);
    }
}
