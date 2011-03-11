package edu.gemini.epics.impl;

import com.google.common.collect.Maps;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.IEpicsClient;
import gov.aps.jca.Context;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class that keeps track of IEpicsClient so that keeps track of services that have been registered
 * but not started as well as clients already started
 */
class EpicsClientsHolder {
    private static final Logger LOG = Logger.getLogger(EpicsService.class.getName());
    private final ConcurrentMap<IEpicsClient, String[]> pendingClients = Maps.newConcurrentMap();
    private final ConcurrentMap<IEpicsClient, ChannelBindingSupport> startedClients = Maps.newConcurrentMap();

    /**
     * Connects a new client and saves it for future reference
     */
    void connectNewClient(Context ctx, IEpicsClient epicsClient, String[] channels) {
        try {
            LOG.info("Binding client " + epicsClient + " to channels " + Arrays.toString(channels));
            ChannelBindingSupport cbs = new ChannelBindingSupport(ctx, epicsClient);
            for (String channel : channels) {
                cbs.bindChannel(channel);
            }
            startedClients.put(epicsClient, cbs);
            epicsClient.connected();
        } catch (EpicsException cae) {
            LOG.log(Level.SEVERE, "Could not connect to EPICS.", cae);
        }
    }

    /**
     * Stores a client to be started when the Context is made available
     */
    void saveClientForConnectionLater(IEpicsClient epicsClient, String[] channels) {
        LOG.info("Saving client " + epicsClient + " for binding channels " + Arrays.toString(channels));
        pendingClients.put(epicsClient, channels);
    }

    /**
     * Disconnect a specific client if known
     */
    void disconnectEpicsClient(IEpicsClient epicsClient) {
        if (startedClients.containsKey(epicsClient)) {
            LOG.info("IEpicsClient removed: " + epicsClient);
            ChannelBindingSupport cbs = startedClients.get(epicsClient);
            try {
                cbs.close();
                epicsClient.disconnected();
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Could not close channel binder.", e);
            }
            startedClients.remove(epicsClient);
        }
    }

    /**
     * Connects all the clients that were previously stored as pending
     */
    void connectAllPendingClients(Context ctx) {
        // TODO Lock access to pendingClients
        for (Map.Entry<IEpicsClient, String[]> pendingClient : pendingClients.entrySet()) {
            connectNewClient(ctx, pendingClient.getKey(), pendingClient.getValue());
        }
        pendingClients.clear();
    }

    /**
     * Disconnect all known connected clients
     */
    public void disconnectAllClients() {
        for(IEpicsClient client: startedClients.keySet()) {
            disconnectEpicsClient(client);
        }
    }
}