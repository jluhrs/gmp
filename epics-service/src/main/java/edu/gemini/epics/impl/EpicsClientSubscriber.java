package edu.gemini.epics.impl;

import com.google.common.collect.ImmutableList;
import edu.gemini.epics.api.EpicsClient;
import edu.gemini.epics.EpicsObserver;

import java.util.Map;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The EpicsService is a simple component that tracks EpicsClient objects registered
 * as OSGi services and set them up as Listeners for channels
 * <p/>
 * Clients need to register an EpicsClient service and include the EpicsClient.EPICS_CHANNEL
 * service properties to indicate which channels it wants to listen to
 */
public class EpicsClientSubscriber {
    private static final Logger LOG = Logger.getLogger(EpicsClientSubscriber.class.getName());
    private final EpicsObserver _epicsObserver;

    public EpicsClientSubscriber(EpicsObserver epicsObserver) {
        checkArgument(epicsObserver != null);
        LOG.fine("EpicsClientSubscriber created with " + epicsObserver);
        _epicsObserver = epicsObserver;
    }

    /**
     * Called when an EpicsClient appears. It will try to bind to the client right away if possible or it will
     * save it to be started later on
     * <br>
     * The services properties of the epicsClient will determine which channels will listen to
     *
     * @param epicsClient       An OSGi service implementing EpicsClient that appears in the system
     * @param serviceProperties The properties of the service registration
     */
    public void bindEpicsClient(EpicsClient epicsClient, Map<String, Object> serviceProperties) {
        if (serviceHasValidProperties(serviceProperties)) {
            String[] channels = (String[]) serviceProperties.get(EpicsClient.EPICS_CHANNELS);
            _epicsObserver.registerEpicsClient(epicsClient, ImmutableList.copyOf(channels));
        } else {
            LOG.warning("Attempt to register an EpicsClient " + epicsClient + " without the right service properties " + serviceProperties);
        }
    }

    private boolean serviceHasValidProperties(Map<String, Object> serviceProperties) {
        return serviceProperties.containsKey(EpicsClient.EPICS_CHANNELS) && serviceProperties.get(EpicsClient.EPICS_CHANNELS) instanceof String[];
    }

    /**
     * Called when an EpicsClient disappears. This method will unbind the context to the client
     *
     * @param epicsClient An OSGi service implementing EpicsClient that disappears
     */
    public void unbindEpicsClient(EpicsClient epicsClient) {
        _epicsObserver.unregisterEpicsClient(epicsClient);
    }
}
