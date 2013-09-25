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

import java.util.List;

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

    @Before
    public void setUp() throws CAException {
        doReturn(ch).when(channelFactory).createChannel(eq(channel), eq(EpicsPcsUpdater.buildZeroZernikesArray()));
    }

    @Test
    public void constructionWithDefaultChannel() throws Exception {
        new EpicsPcsUpdater(channelFactory, null, gains);
        verifyBindings(EpicsPcsUpdater.TCS_ZERNIKES_BASE_CHANNEL);
    }

    @Test
    public void constructionWithExplicitChannel() throws Exception {
        new EpicsPcsUpdater(channelFactory, channel, gains);
        verifyBindings(channel);
    }

    private void verifyBindings(String baseChannel) throws CAException {
        doReturn(ch).when(channelFactory).createChannel(eq(baseChannel), eq(EpicsPcsUpdater.buildZeroZernikesArray()));
    }

    @Test(expected = PcsUpdaterException.class)
    public void constructionWithNadEpicsChannel() throws Exception {
        doThrow(new CAException("Test exception", new RuntimeException())).when(channelFactory).createChannel(eq(channel), eq(EpicsPcsUpdater.buildZeroZernikesArray()));
        new EpicsPcsUpdater(channelFactory, channel, gains);
    }

    @Test
    public void pcsUpdateWithoutValues() throws PcsUpdaterException, EpicsException, CAException, TimeoutException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(channelFactory, channel, gains);
        pcsUpdater.update(new PcsUpdate(new Double[0]));
        verifyBindings(channel);
        verify(ch, never()).setValue(anyDouble());
    }

    @Test
    public void simplePcsUpdate() throws PcsUpdaterException, EpicsException, CAException, TimeoutException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(channelFactory, channel, gains);
        pcsUpdater.update(new PcsUpdate(new Double[]{1.0, 2.0}));

        verify(ch).setValue(eq(ImmutableList.of(1.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)));
    }

    @Test
    public void nullPcsUpdate() throws PcsUpdaterException, EpicsException, CAException, TimeoutException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(channelFactory, channel, gains);
        pcsUpdater.update(null);
        verifyBindings(channel);
        verify(ch, never()).setValue(anyDouble());
    }

    @Test
    public void verifyTooLongPcsUpdate() throws PcsUpdaterException, EpicsException, CAException, TimeoutException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(channelFactory, channel, gains);

        Double[] zernikes = new Double[EpicsPcsUpdater.ZERNIKES_COUNT + 1];
        for (int i = 0; i < EpicsPcsUpdater.ZERNIKES_COUNT + 1; i++) {
            zernikes[i] = (double)i;
        }

        pcsUpdater.update(new PcsUpdate(zernikes));

        verifyBindings(channel);
        verify(ch).setValue(eq(ImmutableList.of(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0)));
    }

    @Test
    public void testGains() throws PcsUpdaterException, EpicsException, CAException, TimeoutException {
        ImmutableList<Double> gains = ImmutableList.of(0.0, 3.0, 0.5, 2.5);
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(channelFactory, channel, gains);

        Double[] zernikes = new Double[EpicsPcsUpdater.ZERNIKES_COUNT + 1];
        for (int i = 0; i < EpicsPcsUpdater.ZERNIKES_COUNT + 1; i++) {
            zernikes[i] = (double)i;
        }

        pcsUpdater.update(new PcsUpdate(zernikes));

        verifyBindings(channel);
        verify(ch).setValue(eq(ImmutableList.of(0.0, 3.0, 1.0, 7.5, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0)));
    }
}
