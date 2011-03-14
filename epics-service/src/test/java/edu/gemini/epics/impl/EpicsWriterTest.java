package edu.gemini.epics.impl;

import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsService;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import org.junit.Test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class EpicsWriterTest {
    private static final String ADDRESS = "172.16.2.24";
    private static final String CHANNEL_NAME = "tst:tst";
    private final Context context = mock(Context.class);
    private final Channel channel = mock(Channel.class);
    private final double[] simulatedValue = new double[]{1, 2};
    private final Double singleValue = new Double(1.0);

    @Test
    public void testWriteValueWithPrimitiveDoubleArray() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        when(channel.getContext()).thenReturn(context);

        EpicsWriter epicsWriter = new EpicsWriter(new EpicsService(context, ADDRESS));
        epicsWriter.bindChannel(CHANNEL_NAME);

        epicsWriter.write(CHANNEL_NAME, simulatedValue);
        verify(channel).put(simulatedValue);
    }

    @Test
    public void testWriteValueToUnknownChannel() throws CAException {
        EpicsWriter epicsWriter = new EpicsWriter(new EpicsService(context, ADDRESS));

        epicsWriter.write(CHANNEL_NAME, simulatedValue);
        verifyZeroInteractions(channel);
    }

    @Test
    public void testWriteValueWithDoubleArray() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        when(channel.getContext()).thenReturn(context);

        EpicsWriter epicsWriter = new EpicsWriter(new EpicsService(context, ADDRESS));
        epicsWriter.bindChannel(CHANNEL_NAME);

        Double[] valuesAsObject = new Double[] {1., 2.};

        epicsWriter.write(CHANNEL_NAME, valuesAsObject);
        verify(channel).put(simulatedValue);
    }

    @Test
    public void testWriteSingleValue() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        when(channel.getContext()).thenReturn(context);

        EpicsWriter epicsWriter = new EpicsWriter(new EpicsService(context, ADDRESS));
        epicsWriter.bindChannel(CHANNEL_NAME);

        epicsWriter.write(CHANNEL_NAME, singleValue);
        verify(channel).put(singleValue);
    }

    @Test
    public void testWriteSingleValueToUnknownChannel() throws CAException {
        EpicsWriter epicsWriter = new EpicsWriter(new EpicsService(context, ADDRESS));

        epicsWriter.write(CHANNEL_NAME, singleValue);
        verifyZeroInteractions(channel);
    }

    @Test(expected = EpicsException.class)
    public void testWriteSingleValueWithCAException() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        doThrow(new CAException()).when(channel).put(singleValue);

        EpicsWriter epicsWriter = new EpicsWriter(new EpicsService(context, ADDRESS));
        epicsWriter.bindChannel(CHANNEL_NAME);

        epicsWriter.write(CHANNEL_NAME, singleValue);
    }

    @Test(expected = EpicsException.class)
    public void testWriteValueWithCAException() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        doThrow(new CAException()).when(channel).put(simulatedValue);

        EpicsWriter epicsWriter = new EpicsWriter(new EpicsService(context, ADDRESS));
        epicsWriter.bindChannel(CHANNEL_NAME);

        epicsWriter.write(CHANNEL_NAME, simulatedValue);
    }

}
