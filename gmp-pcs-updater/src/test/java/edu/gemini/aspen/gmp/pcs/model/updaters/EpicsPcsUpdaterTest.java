package edu.gemini.aspen.gmp.pcs.model.updaters;

import edu.gemini.aspen.gmp.pcs.model.PcsUpdate;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsWriter;
import org.junit.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EpicsPcsUpdater
 *
 * @author cquiroz
 */
public class EpicsPcsUpdaterTest {
    private EpicsWriter writer = mock(EpicsWriter.class);
    private String channel = "X:val1";

    @Test
    public void constructionWithDefaultChannel() throws PcsUpdaterException, EpicsException {
        new EpicsPcsUpdater(writer, null);
        verifyBindings(EpicsPcsUpdater.TCS_ZERNIKES_BASE_CHANNEL);
    }

    @Test
    public void constructionWithExplicitChannel() throws PcsUpdaterException, EpicsException {
        new EpicsPcsUpdater(writer, channel);
        verifyBindings(channel);
    }


    private void verifyBindings(String baseChannel) {
        for (String s: EpicsPcsUpdater.INPUTS) {
            verify(writer).bindChannel(eq(baseChannel + "." + s));
        }

    }

    @Test(expected = PcsUpdaterException.class)
    public void constructionWithNadEpicsChannel() throws PcsUpdaterException, EpicsException {
        doThrow(new EpicsException("Test exception", new RuntimeException())).when(writer).bindChannel(anyString());
        new EpicsPcsUpdater(writer, channel);
    }

    @Test
    public void pcsUpdateWithoutValues() throws PcsUpdaterException, EpicsException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(writer, channel);
        pcsUpdater.update(new PcsUpdate(new Double[0]));
        verifyBindings(channel);
        verify(writer, never()).write(anyString(), anyDouble());
    }

    @Test
    public void simplePcsUpdate() throws PcsUpdaterException, EpicsException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(writer, channel);
        pcsUpdater.update(new PcsUpdate(new Double[]{1.0, 2.0}));

        verify(writer).write(eq(channel + ".A"), eq(1.0));
        verify(writer).write(eq(channel + ".B"), eq(2.0));

    }


    @Test
    public void nullPcsUpdate() throws PcsUpdaterException, EpicsException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(writer, channel);
        pcsUpdater.update(null);
        verifyBindings(channel);
        verify(writer, never()).write(anyString(), anyDouble());
    }

    @Test
    public void verifyTooLongPcsUpdate() throws PcsUpdaterException, EpicsException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(writer, channel);

        Double[] zernikes = new Double[EpicsPcsUpdater.INPUTS.length + 1];
        for (int i = 0; i < EpicsPcsUpdater.INPUTS.length + 1; i++) {
            zernikes[i] = (double)i;
        }

        pcsUpdater.update(new PcsUpdate(zernikes));

        verifyBindings(channel);

        for (int i = 0; i < EpicsPcsUpdater.INPUTS.length; i++) {
            verify(writer).write(eq(channel + "." + EpicsPcsUpdater.INPUTS[i]), eq(zernikes[i]));
        }

        verifyNoMoreInteractions(writer);
    }
}
