package edu.gemini.aspen.gmp.pcs.model;

import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsWriter;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PcsUpdaterComponentTest {
    private EpicsWriter epicsWriter = mock(EpicsWriter.class);
    private PcsUpdaterComposite pcsComposite = new PcsUpdaterCompositeImpl();
    private String channel = "tst";

    @Test
    public void registerWriter() throws EpicsException {
        PcsUpdaterComponent component = buildComponent();

        component.registerEpicsWriter();
        verify(epicsWriter).bindChannel(channel);
    }

    private PcsUpdaterComponent buildComponent() {
        return new PcsUpdaterComponent(epicsWriter, pcsComposite, false, channel);
    }

    @Test
    public void registerWriterInSimulation() throws EpicsException {
        PcsUpdaterComponent component = buildComponentInSimulation();

        component.registerEpicsWriter();
        verifyZeroInteractions(epicsWriter);
    }

    private PcsUpdaterComponent buildComponentInSimulation() {
        return new PcsUpdaterComponent(epicsWriter, pcsComposite, true, channel);
    }

    @Test
    public void exceptionOnRegistrationCaught() throws EpicsException {
        PcsUpdaterComponent component = buildComponentInSimulation();
        doThrow(new EpicsException("Test exception", new RuntimeException())).when(epicsWriter).bindChannel(anyString());

        component.registerEpicsWriter();
        verifyZeroInteractions(epicsWriter);
    }

    @Test
    public void unregisterWriter() throws EpicsException {
        PcsUpdaterComponent component = buildComponent();

        component.registerEpicsWriter();

        component.unRegisterEpicsWriter();
        verify(epicsWriter).bindChannel(channel);
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
        verify(epicsWriter, times(2)).bindChannel(channel);
    }

    @Test
    public void modifyWriterInSimulation() throws EpicsException {
        PcsUpdaterComponent component = buildComponentInSimulation();

        component.registerEpicsWriter();

        component.modifiedEpicsWriter();
        verifyZeroInteractions(epicsWriter);
    }

}
