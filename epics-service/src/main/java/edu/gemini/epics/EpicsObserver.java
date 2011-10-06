package edu.gemini.epics;

import java.util.Collection;

/**
 * This interface defines a service that can register listeners to specific EPICS channels
 *
 * This is similar to the role of {@link EpicsClient} but instead of using OSGi services
 * it can be used directly in code
 *
 * This interface follows the Observer Pattern with EpicsClient being the listeners
 *
 */
public interface EpicsObserver {
    /**
     * Request that the passed client gets updates on the list of channels
     *
     * Unknown channels are ignored
     *
     * @param client A Valid EpicsClient object
     * @param channels A list of channels the Client will subscribe to
     */
    void registerEpicsClient(EpicsClient client, Collection<String> channels);

    /**
     * The counter part to {@link registerEpicsClient}, it will stop sending
     * updates to the client.
     *
     * If a previously unknown client is passed the request is ignored
     * 
     * @param client
     */
    void unregisterEpicsClient(EpicsClient client);
}
