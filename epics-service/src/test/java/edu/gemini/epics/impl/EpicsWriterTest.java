package edu.gemini.epics.impl;

import edu.gemini.epics.EpicsException;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBR_Float;
import org.junit.Test;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class EpicsWriterTest {

    private static final String CHANNEL_NAME = "tst:tst";
    private final Context context = mock(Context.class);
    private final Channel channel = mock(Channel.class);
    private final double[] simulatedValue = new double[]{1, 2};

    @Test
    public void testWriteValue() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        when(channel.getContext()).thenReturn(context);

        EpicsWriter epicsWriter = new EpicsWriter(context);
        epicsWriter.bindChannel(CHANNEL_NAME);

        epicsWriter.write(CHANNEL_NAME, simulatedValue);
        verify(channel).put(simulatedValue);
    }

    @Test
    public void testWriteValueToUnknownChannel() throws CAException {
        EpicsWriter epicsWriter = new EpicsWriter(context);

        epicsWriter.write(CHANNEL_NAME, simulatedValue);
        verifyZeroInteractions(channel);
    }

    @Test(expected = EpicsException.class)
    public void testWriteValueWithCAException() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        doThrow(new CAException()).when(channel).put(simulatedValue);

        EpicsWriter epicsWriter = new EpicsWriter(context);
        epicsWriter.bindChannel(CHANNEL_NAME);

        epicsWriter.write(CHANNEL_NAME, simulatedValue);
    }

    @Test(expected = EpicsException.class)
    public void testReadValueWithTimeoutException() throws CAException, TimeoutException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        EpicsReader epicsReader = new EpicsReader(context);
        epicsReader.bindChannel(CHANNEL_NAME);

        float[] simulatedValue = {1, 2};
        when(channel.get()).thenReturn(new DBR_Float(simulatedValue));
        when(channel.getContext()).thenReturn(context);
        doThrow(new TimeoutException()).when(context).pendIO(anyDouble());

        epicsReader.getValue(CHANNEL_NAME);
    }
}
