package edu.gemini.aspen.gmp.pcs.model.updaters;

import edu.gemini.aspen.gmp.pcs.model.PcsUpdate;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.IEpicsWriter;
import org.junit.Test;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EpicsPcsUpdater
 *
 * @author cquiroz
 */
public class EpicsPcsUpdaterTest {
    private IEpicsWriter writer = mock(IEpicsWriter.class);
    private String channel = "X.val1";

    @Test
    public void constructionWithDefaultChannel() throws PcsUpdaterException, EpicsException {
        new EpicsPcsUpdater(writer, null);

        verify(writer).bindChannel(anyString());
    }

    @Test
    public void constructionWithExplicitChannel() throws PcsUpdaterException, EpicsException {
        new EpicsPcsUpdater(writer, channel);

        verify(writer).bindChannel(eq(channel));
    }

    @Test(expected = PcsUpdaterException.class)
    public void constructionWithNadEpicsChannel() throws PcsUpdaterException, EpicsException {
        doThrow(new EpicsException("Test exception")).when(writer).bindChannel(anyString());
        new EpicsPcsUpdater(writer, channel);
    }

    @Test
    public void simpleUpdate() throws PcsUpdaterException, EpicsException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(writer, channel);
        pcsUpdater.update(new PcsUpdate());

        verify(writer).write(eq(channel), (Double[])anyObject());
    }

    @Test
    public void nullUpdate() throws PcsUpdaterException, EpicsException {
        EpicsPcsUpdater pcsUpdater = new EpicsPcsUpdater(writer, channel);
        pcsUpdater.update(null);

        verify(writer).bindChannel(eq(channel));
        verifyNoMoreInteractions(writer);
    }
}
