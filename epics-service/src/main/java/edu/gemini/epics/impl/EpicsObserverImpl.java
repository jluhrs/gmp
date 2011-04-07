package edu.gemini.epics.impl;

import edu.gemini.epics.EpicsClient;
import edu.gemini.epics.EpicsObserver;
import edu.gemini.epics.JCAContextController;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.Collection;
import java.util.logging.Logger;

@Component
@Provides
@Instantiate(name = "epicsObserver")
public class EpicsObserverImpl implements EpicsObserver {
    private static final Logger LOG = Logger.getLogger(EpicsObserver.class.getName());
    private final JCAContextController contextController;
    private final EpicsClientsHolder epicsClientsHolder = new EpicsClientsHolder();

    public EpicsObserverImpl(@Requires JCAContextController contextController) {
        LOG.info("Created EpicsObserver");
        this.contextController = contextController;
    }

    @Validate
    public void startObserver() {
        LOG.info("Started observer, connect pending clients");
        epicsClientsHolder.connectAllPendingClients(contextController.getJCAContext());
    }

    @Invalidate
    public void stopObserver() {
        epicsClientsHolder.disconnectAllClients();
    }

    @Override
    public void registerEpicsClient(EpicsClient epicsClient, Collection<String> channels) {
        if (contextController.isContextAvailable()) {
            epicsClientsHolder.connectNewClient(contextController.getJCAContext(), epicsClient, channels);
        } else {
            // This may be called before or after the startService method
            epicsClientsHolder.saveForLateConnection(epicsClient, channels);
        }
    }

    @Override
    public void unregisterEpicsClient(EpicsClient epicsClient) {
        epicsClientsHolder.disconnectEpicsClient(epicsClient);
    }
}
