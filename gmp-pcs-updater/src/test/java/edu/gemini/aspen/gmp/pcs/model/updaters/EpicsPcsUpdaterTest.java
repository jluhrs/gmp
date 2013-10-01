package edu.gemini.aspen.gmp.pcs.model.updaters;

import com.google.common.collect.ImmutableList;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdate;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.ReadWriteClientEpicsChannel;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EpicsPcsUpdater
 */
public class EpicsPcsUpdaterTest {
    private ReadWriteClientEpicsChannel ch = mock(ReadWriteClientEpicsChannel.class);
    private ChannelAccessServer channelFactory = mock(ChannelAccessServer.class);
    private String channel = "X:val1";
    private List<Double> gains = ImmutableList.of(1.0);
    private int taiDiff = 0;

    @Before
    public void setUp() throws CAException {
        doReturn(ch).when(channelFactory).createChannel(eq(channel), eq(EpicsPcsUpdater.buildZeroZernikesArray()));
        when(ch.getName()).thenReturn(channel);
    }

    @Test
    public void constructionWithDefaultChannel() throws Exception {
        new EpicsPcsUpdater(channelFactory, null, gains, taiDiff);
        verifyBindings(EpicsPcsUpdater.TCS_ZERNIKES_BASE_CHANNEL);
    }

    @Test
    public void constructionWithExplicitChannel() throws Exception {
        new EpicsPcsUpdater(channelFactory, channel, gains, taiDiff);
        verifyBindings(channel);
    }

    private void verifyBindings(String baseChannel) throws CAException {
        doReturn(ch).when(channelFactory).createChannel(eq(baseChannel), eq(EpicsPcsUpdater.buildZeroZernikesArray()));
    }

    @Test(expected = PcsUpdaterException.class)
    public void constructionWithNadEpicsChannel() throws Exception {
        doThrow(new CAException("Test exception", new RuntimeException())).when(channelFactory).createChannel(eq(channel), eq(EpicsPcsUpdater.buildZeroZernikesArray()));
        new EpicsPcsUpdater(channelFactory, channel, gains, taiDiff);
    }

    @Test
    public void pcsUpdateWithoutValues() throws PcsUpdaterException, EpicsException, CAException, TimeoutException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(channelFactory, channel, gains, taiDiff);
        pcsUpdater.update(new PcsUpdate(new Double[0]));
        verifyBindings(channel);
        verify(ch, never()).setValue(anyDouble());
    }

    @Test
    public void simplePcsUpdate() throws PcsUpdaterException, EpicsException, CAException, TimeoutException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(channelFactory, channel, gains, taiDiff);
        pcsUpdater.update(new PcsUpdate(new Double[]{1.0, 2.0}));

        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(ch).setValue(argument.capture());

        assertTrue(((Double) argument.getValue().get(0)) <= (double) System.currentTimeMillis());
        assertEquals(Double.valueOf(2), argument.getValue().get(1));
        assertEquals(Double.valueOf(1.0), argument.getValue().get(2));
        assertEquals(Double.valueOf(2.0), argument.getValue().get(3));
        for (int i = 4; i < EpicsPcsUpdater.ARRAY_LENGTH; i++) {
            assertEquals(Double.valueOf(0.0), argument.getValue().get(i));
        }
    }

    @Test
    public void testTAICorrection() throws PcsUpdaterException, EpicsException, CAException, TimeoutException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(channelFactory, channel, gains, 35);
        pcsUpdater.update(new PcsUpdate(new Double[]{1.0, 2.0}));

        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(ch).setValue(argument.capture());

        assertTrue((((Double) argument.getValue().get(0)) - 35*1000) <= (double)System.currentTimeMillis());
        assertEquals(Double.valueOf(2), argument.getValue().get(1));
        assertEquals(Double.valueOf(1.0), argument.getValue().get(2));
        assertEquals(Double.valueOf(2.0), argument.getValue().get(3));
        for (int i = 4; i < EpicsPcsUpdater.ARRAY_LENGTH; i++) {
            assertEquals(Double.valueOf(0.0), argument.getValue().get(i));
        }
    }

    @Test
    public void nullPcsUpdate() throws PcsUpdaterException, EpicsException, CAException, TimeoutException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(channelFactory, channel, gains, taiDiff);
        pcsUpdater.update(null);
        verifyBindings(channel);
        verify(ch, never()).setValue(anyDouble());
    }

    @Test
    public void verifyTooLongPcsUpdate() throws PcsUpdaterException, EpicsException, CAException, TimeoutException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(channelFactory, channel, gains, taiDiff);

        Double[] zernikes = new Double[EpicsPcsUpdater.ARRAY_LENGTH + 1];
        for (int i = 0; i < EpicsPcsUpdater.ARRAY_LENGTH + 1; i++) {
            zernikes[i] = (double)i;
        }

        pcsUpdater.update(new PcsUpdate(zernikes));

        verifyBindings(channel);
        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(ch).setValue(argument.capture());
        assertTrue(((Double) argument.getValue().get(0)) <= (double) System.currentTimeMillis());
        assertEquals(Double.valueOf(19), argument.getValue().get(1));
        for (int i = 2; i < EpicsPcsUpdater.ARRAY_LENGTH; i++) {
            assertEquals(Double.valueOf((double)i-2), argument.getValue().get(i));
        }
    }

    @Test
    public void testGains() throws PcsUpdaterException, EpicsException, CAException, TimeoutException {
        ImmutableList<Double> gains = ImmutableList.of(0.0, 3.0, 0.5, 2.5);
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(channelFactory, channel, gains, taiDiff);

        Double[] zernikes = new Double[EpicsPcsUpdater.MAX_ZERNIKES];
        for (int i = 0; i < EpicsPcsUpdater.MAX_ZERNIKES; i++) {
            zernikes[i] = (double)i;
        }

        pcsUpdater.update(new PcsUpdate(zernikes));

        verifyBindings(channel);
        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(ch).setValue(argument.capture());
        assertTrue(((Double) argument.getValue().get(0)) <= (double) System.currentTimeMillis());
        assertEquals(Double.valueOf(19), argument.getValue().get(1));
        assertEquals(Double.valueOf(0.0), argument.getValue().get(2));
        assertEquals(Double.valueOf(3.0), argument.getValue().get(3));
        assertEquals(Double.valueOf(1.0), argument.getValue().get(4));
        assertEquals(Double.valueOf(7.5), argument.getValue().get(5));
    }
}
