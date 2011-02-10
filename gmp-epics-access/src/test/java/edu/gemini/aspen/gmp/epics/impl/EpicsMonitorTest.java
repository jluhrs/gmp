package edu.gemini.aspen.gmp.epics.impl;

import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EpicsMonitorTest {
    @Test
    public void testConnected() {
        EpicsRegistrar registrar = null;
        EpicsMonitor epicsMonitor = new EpicsMonitor(registrar);
        assertFalse(epicsMonitor.isConnected());
        epicsMonitor.connected();
        assertTrue(epicsMonitor.isConnected());
    }

    @Test
    public void testDisconnected() {
        EpicsRegistrar registrar = null;
        EpicsMonitor epicsMonitor = new EpicsMonitor(registrar);
        assertFalse(epicsMonitor.isConnected());
        epicsMonitor.connected();
        epicsMonitor.disconnected();
        assertFalse(epicsMonitor.isConnected());
    }

}
