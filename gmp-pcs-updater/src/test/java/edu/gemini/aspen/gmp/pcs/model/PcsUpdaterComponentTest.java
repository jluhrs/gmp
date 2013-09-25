package edu.gemini.aspen.gmp.pcs.model;

import edu.gemini.aspen.gmp.pcs.model.updaters.EpicsPcsUpdater;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelFactory;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsWriter;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PcsUpdaterComponentTest {
    private EpicsWriter epicsWriter = mock(EpicsWriter.class);
    private JmsProvider provider = mock(JmsProvider.class);
    private ChannelAccessServer channelFactory = mock(ChannelAccessServer.class);
    private PcsUpdaterComposite pcsComposite = new PcsUpdaterCompositeImpl();
    private String channel = "tst";

    @Test
    public void registerWriter() throws EpicsException {
        PcsUpdaterComponent component = buildComponent();

        component.registerEpicsWriter();
        verifyBindings(channel);
    }

    private PcsUpdaterComponent buildComponent() {
        return new PcsUpdaterComponent(channelFactory, epicsWriter, pcsComposite, false, channel);
    }


    private void verifyBindings(String baseChannel, int times) {
        for (String s : EpicsPcsUpdater.INPUTS) {
            verify(epicsWriter, times(times)).getDoubleChannel(eq(baseChannel + "." + s));
        }
    }

    private void verifyBindings(String baseChannel) {
        verifyBindings(baseChannel, 1);
    }

    @Test
    public void registerWriterInSimulation() throws EpicsException {
        PcsUpdaterComponent component = buildComponentInSimulation();

        component.registerEpicsWriter();
        verifyZeroInteractions(epicsWriter);
    }

    private PcsUpdaterComponent buildComponentInSimulation() {
        return new PcsUpdaterComponent(channelFactory, epicsWriter, pcsComposite, true, channel);
    }

    @Test
    public void exceptionOnRegistrationCaught() throws EpicsException {
        PcsUpdaterComponent component = buildComponentInSimulation();
        doThrow(new EpicsException("Test exception", new RuntimeException())).when(epicsWriter).getDoubleChannel(anyString());

        component.registerEpicsWriter();
        verifyZeroInteractions(epicsWriter);
    }

    @Test
    public void unregisterWriter() throws EpicsException {
        PcsUpdaterComponent component = buildComponent();

        component.registerEpicsWriter();

        component.unRegisterEpicsWriter();
        verifyBindings(channel);
        // TODO Should the channel be unbound?
    }

    @Test
    public void unregisterWriterInSimulation() throws EpicsException {
        PcsUpdaterComponent component = buildComponentInSimulation();

        component.registerEpicsWriter();

        component.unRegisterEpicsWriter();
        verifyZeroInteractions(epicsWriter);
    }

    @Test
    public void modifyWriter() throws EpicsException {
        PcsUpdaterComponent component = buildComponent();

        component.registerEpicsWriter();

        component.modifiedEpicsWriter();

        verifyBindings(channel, 2);
    }

    @Test
    public void modifyWriterInSimulation() throws EpicsException {
        PcsUpdaterComponent component = buildComponentInSimulation();

        component.registerEpicsWriter();

        component.modifiedEpicsWriter();
        verifyZeroInteractions(epicsWriter);
    }

}
