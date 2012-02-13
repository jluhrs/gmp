package edu.gemini.aspen.gmp.epics.impl;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.gmp.epics.EpicsConfiguration;
import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import edu.gemini.aspen.gmp.epics.jms.EpicsConfigRequestConsumer;
import edu.gemini.aspen.gmp.epics.jms.EpicsStatusUpdater;
import edu.gemini.epics.api.EpicsClient;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import java.util.Arrays;
import java.util.List;
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
@Component
@Instantiate
@Provides
public class EpicsMonitor implements EpicsClient, JmsArtifact {
    private static final Logger LOG = Logger.getLogger(EpicsMonitor.class.getName());
    private volatile boolean connected = false;

    private final EpicsRegistrar _registrar;
    private final EpicsConfiguration _epicsConfig;

    @ServiceProperty(name = "edu.gemini.epics.api.EpicsClient.EPICS_CHANNELS")
    private String[] props;

    private EpicsConfigRequestConsumer _epicsRequestConsumer;
    private EpicsStatusUpdater _epicsStatusUpdater;

    public EpicsMonitor(@Requires(proxy = false) EpicsRegistrar registrar, @Requires(proxy = false) EpicsConfiguration epicsConfig) {
        Preconditions.checkArgument(registrar != null, "Cannot create an EpicsMonitor with a null registrar");
        _registrar = registrar;
        _epicsConfig = epicsConfig;
    }

    public <T> void valueChanged(String channel, List<T> values) {
        _registrar.processEpicsUpdate(new EpicsUpdateImpl<T>(channel, values));
    }

    @Updated
    public void updated() {
        Set<String> channelsNames = _epicsConfig.getValidChannelsNames();
        LOG.info("Updated configuration of Epics Access with " + channelsNames);
        props = channelsNames.toArray(new String[0]);
        LOG.info("Services properties set as: " + Arrays.asList(props));
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
    }

    private void removeInterestingChannels() {
        try {
            for (String channel : _epicsConfig.getValidChannelsNames()) {
                _registrar.unregisterInterest(channel);
            }
        } catch (Exception e) {
            LOG.warning("Exception while shutting down, probably not important");
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

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        LOG.info("JMS Provider found. Starting Epics Access bundle");
        _epicsRequestConsumer = new EpicsConfigRequestConsumer(provider, _epicsConfig);
        _epicsStatusUpdater = new EpicsStatusUpdater(provider, _epicsConfig);

        setupRegistrar();
    }

    @Override
    public void stopJms() {
        _epicsRequestConsumer.close();
        _epicsStatusUpdater.close();
    }
}
