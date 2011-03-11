package edu.gemini.epics.impl;

import com.google.common.collect.Maps;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.IEpicsClient;
import gov.aps.jca.Context;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class that keeps track of IEpicsClient so that keeps track of services that have been registered
 * but not started as well as clients already started
 */
public class EpicsClientsHolder {
    private static final Logger LOG = Logger.getLogger(EpicsService.class.getName());
    private final Map<IEpicsClient, String[]> pendingClients = Maps.newConcurrentMap();
    private final Map<IEpicsClient, ChannelBindingSupport> startedClients = Maps.newConcurrentMap();

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

    void saveClientForConnectionLater(IEpicsClient epicsClient, String[] channels) {
        LOG.info("Saving client " + epicsClient + " for binding channels " + Arrays.toString(channels));
        pendingClients.put(epicsClient, channels);
    }

    public void disconnectEpicsClient(IEpicsClient epicsClient) {
        LOG.info("IEpicsClient removed: " + epicsClient);

        if (startedClients.containsKey(epicsClient)) {
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

    public void connectAllPendingClients(Context ctx) {
        // TODO Lock access to pendingClients
        for (Map.Entry<IEpicsClient, String[]> pendingClient : pendingClients.entrySet()) {
            connectNewClient(ctx, pendingClient.getKey(), pendingClient.getValue());
        }
        pendingClients.clear();
    }

    public void disconnectAllClients() {
        for(IEpicsClient client: startedClients.keySet()) {
            disconnectEpicsClient(client);
        }
    }
}