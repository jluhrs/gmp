package edu.gemini.aspen.gmp.epics.impl;

import java.util.logging.Logger;

import edu.gemini.epics.IEpicsClient;
import edu.gemini.aspen.gmp.epics.EpicsRegistrar;

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

    private boolean connected;


    private EpicsRegistrar _registrar;


    public void channelChanged(String channel, Object value) {
        _registrar.processEpicsUpdate(new EpicsUpdateImpl(channel, value));
    }

    public void connected() {
        LOG.info("Connected to EPICS");
        connected = true;
    }

    public void disconnected() {
        connected = false;
        LOG.info("Disconnected from EPICS");
    }

    public EpicsMonitor(EpicsRegistrar registrar) {
        _registrar = registrar;
    }

    public boolean isConnected() {
        return connected;
    }


}
