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

public class EpicsClientsHolder {
    private static final Logger LOG = Logger.getLogger(EpicsService.class.getName());
    private final Map<IEpicsClient, String[]> pendingClients = Maps.newConcurrentMap();
    private final Map<IEpicsClient, ChannelBindingSupport> currentClients = Maps.newConcurrentMap();

    void bindClientToContext(Context ctx, IEpicsClient epicsClient, String[] channels) {
        try {
            LOG.info("Binding client " + epicsClient + " to channels " + Arrays.toString(channels));
            ChannelBindingSupport cbs = new ChannelBindingSupport(ctx, epicsClient);
            for (String channel : channels) {
                cbs.bindChannel(channel);
            }
            currentClients.put(epicsClient, cbs);
            epicsClient.connected();
        } catch (EpicsException cae) {
            LOG.log(Level.SEVERE, "Could not connect to EPICS.", cae);
        }
    }

    void saveClientForLateBinding(IEpicsClient epicsClient, String[] channels) {
        LOG.info("Saving client " + epicsClient + " for binding channels " + Arrays.toString(channels));
        pendingClients.put(epicsClient, channels);
    }

    public void unbindEpicsClient(IEpicsClient epicsClient) {
        LOG.info("IEpicsClient removed: " + epicsClient);

        if (currentClients.containsKey(epicsClient)) {
            ChannelBindingSupport cbs = currentClients.get(epicsClient);
            try {
                cbs.close();
                epicsClient.disconnected();
                currentClients.remove(epicsClient);
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Could not close channel binder.", e);
            }
        }
    }

    public void bindPendingClients(Context ctx) {
        // TODO Lock access to pendingClients
        for (Map.Entry<IEpicsClient, String[]> pendingClient : pendingClients.entrySet()) {
            bindClientToContext(ctx, pendingClient.getKey(), pendingClient.getValue());
        }
        pendingClients.clear();
    }
}