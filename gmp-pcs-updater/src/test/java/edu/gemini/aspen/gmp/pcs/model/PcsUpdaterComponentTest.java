package edu.gemini.aspen.gmp.pcs.model;

import edu.gemini.aspen.gmp.pcs.model.updaters.EpicsPcsUpdater;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsWriter;
import gov.aps.jca.CAException;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PcsUpdaterComponentTest {
    private EpicsWriter epicsWriter = mock(EpicsWriter.class);
    private ChannelAccessServer channelFactory = mock(ChannelAccessServer.class);
    private PcsUpdaterComposite pcsComposite = new PcsUpdaterCompositeImpl();
    private String channel = "tst";

    @Test
    public void registerWriter() throws Exception {
        PcsUpdaterComponent component = buildComponent();

        component.startComponent();
        verifyBindings(channel, 1);
    }

    private PcsUpdaterComponent buildComponent() {
        return new PcsUpdaterComponent(channelFactory, pcsComposite, false, channel);
    }

    private void verifyBindings(String baseChannel, int count) throws CAException {
        verify(channelFactory, times(count)).createChannel(eq(baseChannel), eq(EpicsPcsUpdater.buildZeroZernikesArray()));
    }

    @Test
    public void registerWriterInSimulation() throws EpicsException {
        PcsUpdaterComponent component = buildComponentInSimulation();

        component.startComponent();
        verifyZeroInteractions(epicsWriter);
    }

    private PcsUpdaterComponent buildComponentInSimulation() {
        return new PcsUpdaterComponent(channelFactory, pcsComposite, true, channel);
    }

    @Test
    public void exceptionOnRegistrationCaught() throws EpicsException {
        PcsUpdaterComponent component = buildComponentInSimulation();
        doThrow(new EpicsException("Test exception", new RuntimeException())).when(epicsWriter).getDoubleChannel(anyString());

        component.startComponent();
        verifyZeroInteractions(epicsWriter);
    }

    @Test
    public void unregisterWriter() throws Exception {
        PcsUpdaterComponent component = buildComponent();

        component.startComponent();

        component.stopComponent();
        verifyBindings(channel, 1);
        // TODO Should the channel be unbound?
    }

    @Test
    public void unregisterWriterInSimulation() throws EpicsException {
        PcsUpdaterComponent component = buildComponentInSimulation();

        component.startComponent();

        component.stopComponent();
        verifyZeroInteractions(epicsWriter);
    }

    @Test
    public void modifyWriter() throws Exception {
        PcsUpdaterComponent component = buildComponent();

        component.startComponent();

        component.modifiedEpicsWriter();

        verifyBindings(channel, 2);
    }

    @Test
    public void modifyWriterInSimulation() throws EpicsException {
        PcsUpdaterComponent component = buildComponentInSimulation();

        component.startComponent();

        component.modifiedEpicsWriter();
        verifyZeroInteractions(epicsWriter);
    }

}
