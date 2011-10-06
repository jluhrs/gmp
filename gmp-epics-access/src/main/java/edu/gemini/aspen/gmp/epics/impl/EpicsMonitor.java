package edu.gemini.aspen.gmp.epics.impl;

import edu.gemini.aspen.gmp.epics.EpicsConfiguration;
import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import edu.gemini.aspen.gmp.epics.jms.EpicsConfigRequestConsumer;
import edu.gemini.aspen.gmp.epics.jms.EpicsStatusUpdater;
import edu.gemini.epics.EpicsClient;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.*;

import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class monitors the EPICS channels acting as an EpicsClient.
 * The updates are delegated for further processing to an EpicsRegistrar
 * <br>
 * It allows to register <code>EpicsUpdateListener</code> objects, which
 * will be invoked whenever an update to the monitored EPICS channel is
 * received.
 */
@Component(name = "epicsMonitor", managedservice = "edu.gemini.aspen.gmp.epics.EpicsMonitor")
@Instantiate(name = "epicsMonitor")
@Provides
public class EpicsMonitor implements EpicsClient {
    private static final Logger LOG = Logger.getLogger(EpicsMonitor.class.getName());
    private volatile boolean connected = false;

    private final EpicsRegistrar _registrar;
    private final JmsProvider _provider;
    private final EpicsConfiguration _epicsConfig;

    @ServiceProperty(name = "edu.gemini.epics.EpicsClient.EPICS_CHANNELS")
    private String[] props;

    private EpicsConfigRequestConsumer _epicsRequestConsumer;
    private EpicsStatusUpdater _epicsStatusUpdater;

    public EpicsMonitor(@Requires EpicsRegistrar registrar, @Requires JmsProvider provider, @Requires EpicsConfiguration epicsConfig) {
        if (registrar == null) {
            throw new IllegalArgumentException("Cannot create an EpicsMonitor with a null registrar");
        }
        _registrar = registrar;
        _provider = provider;
        _epicsConfig =  epicsConfig;
    }

    public void channelChanged(String channel, Object value) {
        _registrar.processEpicsUpdate(new EpicsUpdateImpl(channel, value));
    }

    @Updated
    public void updated() {
        Set<String> channelsNames = _epicsConfig.getValidChannelsNames();
        LOG.info("Updated configuration of Epics Access with " + channelsNames);
        props = channelsNames.toArray(new String[0]);
        LOG.info("Services properties set as: " + Arrays.asList(props));
    }

    @Validate
    public void validated() {
        LOG.info("JMS Provider found. Starting Epics Access bundle");
        _epicsRequestConsumer = new EpicsConfigRequestConsumer(_provider, _epicsConfig);
        _epicsStatusUpdater = new EpicsStatusUpdater(_provider, _epicsConfig);

        setupRegistrar();
    }

    private void setupRegistrar() {
        for (String channel : _epicsConfig.getValidChannelsNames()) {
            _registrar.registerInterest(channel, _epicsStatusUpdater);
        }
        _registrar.start();
    }

    @Invalidate
    public void invalidate() {
        LOG.info("Stopping Epics Access bundle");
        removeInterestingChannels();

        _epicsRequestConsumer.close();
        _epicsStatusUpdater.close();
    }

    private void removeInterestingChannels() {
        if (_epicsConfig != null && _registrar != null) {
            LOG.info("CONFIG " + _epicsConfig + " " + _registrar);
            for (String channel : _epicsConfig.getValidChannelsNames()) {
                _registrar.unregisterInterest(channel);
            }
        }
    }

    public void connected() {
        LOG.info(this + " connected to EPICS");
        connected = true;
    }

    public void disconnected() {
        LOG.info(this + " disconnected from EPICS");
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public String toString() {
        return "EpicsMonitor{" +
                "connected=" + connected +
                ", props=" + (props == null ? null : Arrays.asList(props)) +
                '}';
    }
}
