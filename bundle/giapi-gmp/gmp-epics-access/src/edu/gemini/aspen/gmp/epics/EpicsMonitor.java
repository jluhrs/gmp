package edu.gemini.aspen.gmp.epics;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.beans.PropertyChangeSupport;

import edu.gemini.epics.IEpicsClient;

/**
 * This class monitors the EPICS channels specified by the Epics Configuration
 * object passed during construction.
 * <p/>
 * It allows to register <code>EpicsUpdateListener</code> objects, which
 * will be invoked whenever an update to the monitored EPICS channel is
 * received. 
 *
 */
public class EpicsMonitor implements IEpicsClient {

    public static final Logger LOG = Logger.getLogger(EpicsMonitor.class.getName());

    private boolean connected;

    private Set<String> _channels;

    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    Map<String, EpicsUpdateListener> _updatersMap = new HashMap<String, EpicsUpdateListener>();

    public void channelChanged(String channel, Object value) {
        //notify the updaters
        EpicsUpdateListener listener = _updatersMap.get(channel);
        //If there is any interested listeners, will notify them.
        //will filter null updates. They can occur if the EPICS communication
        //is lost for some reason. 
        if (listener != null && value != null) {
            listener.onEpicsUpdate(new EpicsUpdate(channel, value));
        }
    }


    /**
     * Register the specific listener to be invoked whenever an update to
     * the epics channel specified by name is received
     * @param channel name of the epics channel whose update will trigger
     * a call to the listener.
     * @param updater listener to be invoked when an update is received
     */
    public void registerInterest(String channel, EpicsUpdateListener updater) {
        _updatersMap.put(channel, updater);
    }

    /**
     * Stop updating the listener associated to the channel
     * @param channel name of the epics channel that will stop being monitored.
     */
    public void unregisterInterest(String channel) {
        _updatersMap.remove(channel);
    }

    public void connected() {
        LOG.info("Connected to EPICS");
        connected = true;
    }

    public void disconnected() {
        connected = false;
        LOG.info("Disconnected from EPICS");
    }

    public EpicsMonitor(EpicsConfiguration config) {
        _channels = config.getValidChannelsNames();
    }

    public String[] getChannels() {
        return _channels.toArray(new String[_channels.size()]);
    }

    public boolean isConnected() {
        return connected;
    }


}
