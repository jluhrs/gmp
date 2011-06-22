package edu.gemini.epics.impl;

import com.cosylab.epics.caj.CAJChannel;
import com.cosylab.epics.caj.CAJContext;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.EpicsWriter;
import gov.aps.jca.CAException;
import org.junit.Test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class EpicsWriterImplTest {
    private static final String CHANNEL_NAME = "tst:tst";
    private final CAJContext context = mock(CAJContext.class);
    private final CAJChannel channel = mock(CAJChannel.class);
    private final double[] simulatedValue = new double[]{1, 2};
    private final Double singleValue = new Double(1.0);

    @Test
    public void testWriteValueWithPrimitiveDoubleArray() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        when(channel.getContext()).thenReturn(context);

        EpicsWriterImpl epicsWriter = new EpicsWriterImpl(new EpicsService(context));
        epicsWriter.startEpicsWriter();
        epicsWriter.bindChannel(CHANNEL_NAME);

        epicsWriter.write(CHANNEL_NAME, simulatedValue);
        verify(channel).put(simulatedValue);
    }

    @Test
    public void testWriteValueToUnknownChannel() throws CAException {
        EpicsWriter epicsWriter = new EpicsWriterImpl(new EpicsService(context));

        epicsWriter.write(CHANNEL_NAME, simulatedValue);
        verifyZeroInteractions(channel);
    }

    @Test
    public void testWriteValueWithDoubleArray() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        when(channel.getContext()).thenReturn(context);

        EpicsWriter epicsWriter = new EpicsWriterImpl(new EpicsService(context));
        epicsWriter.bindChannel(CHANNEL_NAME);

        Double[] valuesAsObject = new Double[] {1., 2.};

        epicsWriter.write(CHANNEL_NAME, valuesAsObject);
        verify(channel).put(simulatedValue);
    }

    @Test
    public void testWriteSingleValue() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        when(channel.getContext()).thenReturn(context);

        EpicsWriter epicsWriter = new EpicsWriterImpl(new EpicsService(context));
        epicsWriter.bindChannel(CHANNEL_NAME);

        epicsWriter.write(CHANNEL_NAME, singleValue);
        verify(channel).put(singleValue);
    }

    @Test
    public void testWriteSingleValueToUnknownChannel() throws CAException {
        EpicsWriter epicsWriter = new EpicsWriterImpl(new EpicsService(context));

        epicsWriter.write(CHANNEL_NAME, singleValue);
        verifyZeroInteractions(channel);
    }

    @Test(expected = EpicsException.class)
    public void testWriteSingleValueWithCAException() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        doThrow(new CAException()).when(channel).put(singleValue);

        EpicsWriter epicsWriter = new EpicsWriterImpl(new EpicsService(context));
        epicsWriter.bindChannel(CHANNEL_NAME);

        epicsWriter.write(CHANNEL_NAME, singleValue);
    }

    @Test(expected = EpicsException.class)
    public void testWriteValueWithCAException() throws CAException {
        when(context.createChannel(CHANNEL_NAME)).thenReturn(channel);

        doThrow(new CAException()).when(channel).put(simulatedValue);

        EpicsWriter epicsWriter = new EpicsWriterImpl(new EpicsService(context));
        epicsWriter.bindChannel(CHANNEL_NAME);

        epicsWriter.write(CHANNEL_NAME, simulatedValue);
    }

}
