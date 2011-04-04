package edu.gemini.epics.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.gemini.epics.EpicsClient;
import edu.gemini.epics.EpicsException;
import gov.aps.jca.Context;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class that keeps track of EpicsClient so that keeps track of services that have been registered
 * but not started as well as clients already started
 */
public class EpicsClientsHolder {
    private static final Logger LOG = Logger.getLogger(EpicsClientsHolder.class.getName());
    private final ConcurrentMap<EpicsClient, Iterable<String>> pendingClients = Maps.newConcurrentMap();
    private final ConcurrentMap<EpicsClient, ChannelBindingSupport> startedClients = Maps.newConcurrentMap();

    /**
     * Connects a new client and saves it for future reference
     */
    public void connectNewClient(Context ctx, EpicsClient epicsClient, String[] channels) {
        connectNewClient(ctx, epicsClient, Lists.newArrayList(channels));
    }

    public void connectNewClient(Context ctx, EpicsClient epicsClient, Iterable<String> channels) {
        try {
            ChannelBindingSupport cbs = new ChannelBindingSupport(ctx, epicsClient);
            for (String channel : channels) {
                LOG.info("Binding client " + epicsClient + " to channel " + channel);
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
    public void saveForLateConnection(EpicsClient epicsClient, Iterable<String> channels) {
        LOG.fine("Saving client " + epicsClient + " for binding channels " + channels);
        pendingClients.put(epicsClient, channels);
    }

    /**
     * Stores a client to be started when the Context is made available
     */
    public void saveForLateConnection(EpicsClient epicsClient, String[] channels) {
        LOG.fine("Saving client " + epicsClient + " for binding channels " + Arrays.toString(channels));
        pendingClients.put(epicsClient, ImmutableList.copyOf(channels));
    }

    /**
     * Disconnect a specific client if known
     */
    public void disconnectEpicsClient(EpicsClient epicsClient) {
        if (startedClients.containsKey(epicsClient)) {
            LOG.info("EpicsClient removed: " + epicsClient);
            ChannelBindingSupport cbs = startedClients.get(epicsClient);
            try {
                cbs.close();
                epicsClient.disconnected();
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Could not close channel binder for client " + epicsClient, e);
            }
            startedClients.remove(epicsClient);
        }
    }

    /**
     * Connects all the clients that were previously stored as pending
     */
    public void connectAllPendingClients(Context ctx) {
        // TODO Lock access to pendingClients
        for (Map.Entry<EpicsClient, Iterable<String>> pendingClient : pendingClients.entrySet()) {
            connectNewClient(ctx, pendingClient.getKey(), pendingClient.getValue());
        }
        pendingClients.clear();
    }

    /**
     * Disconnect all known connected clients
     */
    public void disconnectAllClients() {
        for (EpicsClient client : startedClients.keySet()) {
            disconnectEpicsClient(client);
        }
    }
}