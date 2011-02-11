package edu.gemini.aspen.gmp.epics.impl;

import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import edu.gemini.epics.IEpicsClient;

import java.util.logging.Logger;

/**
 * This class monitors the EPICS channels acting as an IEpicsClient.
 * The updates are delegated for further processing to an EpicsRegistrar
 *
 * <p/>
 * It allows to register <code>EpicsUpdateListener</code> objects, which
 * will be invoked whenever an update to the monitored EPICS channel is
 * received. 
 *
 */
public class EpicsMonitor implements IEpicsClient {
    public static final Logger LOG = Logger.getLogger(EpicsMonitor.class.getName());

    private volatile boolean connected = false;
    private final EpicsRegistrar _registrar;

    public EpicsMonitor(EpicsRegistrar registrar) {
        if (registrar == null) {
            throw new IllegalArgumentException("Cannot create an EpicsMonitor with a null registrar");
        }
        _registrar = registrar;
    }

    public void channelChanged(String channel, Object value) {
        _registrar.processEpicsUpdate(new EpicsUpdateImpl(channel, value));
    }

    public void connected() {
        LOG.info("Connected to EPICS");
        connected = true;
    }

    public void disconnected() {
        LOG.info("Disconnected from EPICS");
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

}
