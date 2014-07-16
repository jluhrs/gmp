package edu.gemini.epics.impl;

import com.google.common.base.Preconditions;
import edu.gemini.epics.api.EpicsClient;
import edu.gemini.epics.EpicsObserver;
import edu.gemini.epics.JCAContextController;

import java.util.Collection;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class is an implementation of EpicsObserver. It allows EpicClient objects to
 * register to be notified upon changes on an epics channel
 */
public class EpicsObserverImpl implements EpicsObserver {
    private static final Logger LOG = Logger.getLogger(EpicsObserver.class.getName());
    private final JCAContextController contextController;
    private final EpicsClientsHolder epicsClientsHolder = new EpicsClientsHolder();

    public EpicsObserverImpl(JCAContextController contextController) {
        LOG.info("Created EpicsObserver");
        checkArgument(contextController != null, "Cannot be build with a null contextController");
        this.contextController = contextController;
    }

    public void startObserver() {
        Preconditions.checkState(contextController.isContextAvailable(), "JCA Context must be already available");

        LOG.fine("Started observer, connect pending clients");
        epicsClientsHolder.connectAllPendingClients(contextController.getJCAContext());
    }

    public void stopObserver() {
        epicsClientsHolder.disconnectAllClients();
    }

    @Override
    public void registerEpicsClient(EpicsClient epicsClient, Collection<String> channels) {
        if (channels != null) {
            registerListener(epicsClient, channels);
        }
    }

    private void registerListener(EpicsClient epicsClient, Collection<String> channels) {
        // This may be called before or after the startService method
        if (contextController.isContextAvailable()) {
            epicsClientsHolder.connectNewClient(contextController.getJCAContext(), epicsClient, channels);
        } else {
            epicsClientsHolder.saveForLateConnection(epicsClient, channels);
        }
    }

    @Override
    public void unregisterEpicsClient(EpicsClient epicsClient) {
        epicsClientsHolder.disconnectEpicsClient(epicsClient);
    }
}
