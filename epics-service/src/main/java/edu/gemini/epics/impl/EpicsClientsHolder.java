package edu.gemini.epics.impl;

import com.cosylab.epics.caj.CAJContext;
import com.google.common.collect.Maps;
import edu.gemini.epics.api.EpicsClient;
import edu.gemini.epics.EpicsException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class that keeps track of EpicsClient so that keeps track of services that have been registered
 * but not started as well as clients already started
 * <br>
 * This class is coded defensively against errors in the EpicsClient objects
 */
public class EpicsClientsHolder {
    private static final Logger LOG = Logger.getLogger(EpicsClientsHolder.class.getName());
    private final ConcurrentMap<EpicsClient, Collection<String>> pendingClients = Maps.newConcurrentMap();
    private final ConcurrentMap<EpicsClient, ChannelBindingSupport> boundClients = Maps.newConcurrentMap();

    public void connectNewClient(CAJContext ctx, EpicsClient epicsClient, Collection<String> channels) {
        if (!channels.isEmpty()) {
            ChannelBindingSupport cbs = subscribeToChannels(ctx, epicsClient, channels);

            boundClients.put(epicsClient, cbs);

            sendConnectToClient(epicsClient);
        }
    }

    private ChannelBindingSupport subscribeToChannels(CAJContext ctx, EpicsClient epicsClient, Collection<String> channels) {
        ChannelBindingSupport cbs = new ChannelBindingSupport(ctx, epicsClient);
        for (String channel : channels) {
            LOG.fine("Binding client " + epicsClient + " to channel " + channel);
            try {
                cbs.bindChannel(channel);
            } catch (EpicsException e) {
                LOG.log(Level.SEVERE, "Exception while binding to channel " + channel, e);
            }
        }
        return cbs;
    }

    private void sendConnectToClient(EpicsClient epicsClient) {
        // Wrap to be defensive against the clients
        try {
            epicsClient.connected();
        } catch (Exception cae) {
            LOG.log(Level.SEVERE, "Exception while Could not connect to EPICS.", cae);
        }
    }

    /**
     * Stores a client to be started when the Context is made available
     */
    public void saveForLateConnection(EpicsClient epicsClient, Collection<String> channels) {
        LOG.fine("Saving client " + epicsClient + " for binding channels " + channels);
        pendingClients.putIfAbsent(epicsClient, channels);
    }

    /**
     * Disconnect a specific client if known
     */
    public void disconnectEpicsClient(EpicsClient epicsClient) {
        if (boundClients.containsKey(epicsClient)) {
            LOG.fine("EpicsClient removed: " + epicsClient);
            ChannelBindingSupport cbs = boundClients.get(epicsClient);
            cbs.close();

            sendDisconnectToClient(epicsClient);
            boundClients.remove(epicsClient);
        }
    }

    private void sendDisconnectToClient(EpicsClient epicsClient) {
        // Wrap to be defensive against the clients
        try {
            epicsClient.disconnected();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Could not close channel binder for client " + epicsClient, e);
        }
    }

    /**
     * Connects all the clients that were previously stored as pending
     */
    public void connectAllPendingClients(CAJContext ctx) {
        for (Map.Entry<EpicsClient, Collection<String>> pendingClient : pendingClients.entrySet()) {
            connectNewClient(ctx, pendingClient.getKey(), pendingClient.getValue());
        }
        pendingClients.clear();
    }

    /**
     * Disconnect all known connected clients
     */
    public void disconnectAllClients() {
        for (EpicsClient client : boundClients.keySet()) {
            disconnectEpicsClient(client);
        }
    }
}